# Modelling Notes - Tyre Degradation

Working notes on how I built the first tyre degradation model, the challenges i faced and reliability of data.

---

## Known signal contaminants in lap time data

My degradation model must exclude or correct the following issues:

- Lap 1 (standing start, first lap traffic) - **exclude**
- In laps, out laps (include pit lane time) - **exclude**
- Fuel burn (~0.03–0.05s/lap gain as the car gets lighter) - **must be corrected for, as it masks degradation**
- Weather changes (2024 British GP, session 9558: wet-dry-wet; overall lap times jumped ~6s around lap 19) - **detect and exclude**
- Safety car / VSC laps - **exclude**
- Traffic (following another car costs time unrelated to tyres) - **hard to detect, this is a limitation**

For my model there are essentially 3 categories I have to separate these contaminants into:

1. **Permanent exclusions** - laps that should never measure tyre pace. In laps, out laps, lap 1.
   These aren't simplifications I'll remove later; they stay excluded in any version of the model.
2. **Corrections** - effects I adjust for rather than discard, because they affect every lap.
   E.g. fuel burn.
3. **Deferred sophistication** - real effects I'll handle in later versions. Traffic, track
   evolution, weather, driver lift-and-coast.

---

## Choosing a session to build against

I began with the 2024 British GP (session 9558) and quickly faced an issue where lap times exploded
around lap 19 and driver 1's second stint was on INTERMEDIATE tyres. It had rained, making it
a poor session to *build* a model on, because I can't tell whether a change in lap time is
the tyre or the weather.

I switched to **Monza, 2024 Italian GP, session 9590**. I first checked it was clean by looking at
the average lap time per lap:

- Gentle downward trend throughout the race (fuel burn + track rubbering in) - expected
- No sustained multi-second plateau - so confirming **no safety car**
- Small bumps where groups of cars pitted - expected
- Lap 1 slow (standing start) - expected
- Car count drops 20 → 19 at lap 8 (a retirement) and → 13 on the last lap (lapped cars
  finishing a lap earlier)

I will keep silverstone as a tough test case for later. A reliable model should *not*
draw conclusions from it.

**Sanity check query** (run this on any session before trusting analysis from it):

```sql
SELECT l.lap_number,
       ROUND(AVG(l.lap_duration), 3) AS avg_lap,
       COUNT(*)                      AS cars
FROM lap l
WHERE l.session_key = :sessionKey
  AND l.lap_duration IS NOT NULL
GROUP BY l.lap_number
ORDER BY l.lap_number;
```

Patterns indicate: a plateau of laps 15–30% slower for 3+ laps is a safety car. A slow
increase that then decreases back many laps is rain. One or two lap bumps are pit
windows and are harmless.

---

## How I got to the final query (and what was wrong at each step)

### Attempt 1 - fit a slope per stint

For each stint, I fitted a straight line through lap time against tyre age. The slope is
"seconds gained or lost per lap of tyre age".

**Result:** slopes ranged from −0.128 to +0.070, with signs changing randomly.
MEDIUM and HARD didn't separate at all.

**Why it failed:** the signal (~0.02 s/lap) was much smaller than the noise (±0.05 s/lap).
A single stint lasts 10–40 laps, and a couple of laps spent in traffic can alter the
whole fit. The most believable numbers were all from the longest stints, which hints at
an issue.

### Attempt 2 - pool all stints by compound

I fitted one line per compound across all laps for each driver, so per-driver noise averages out.
Also, I added a fuel correction by adding `0.035 × lap_number` back onto each lap time, to focus
on tyre age rather than fuel load.

**Result:** HARD 0.0382, MEDIUM 0.0117 - hards degrading three times faster than mediums.
This is incorrect as harder compounds should be more durable.

**Why it failed:** pooling assumes every stint shares the same baseline pace, which isnt the case.
A fast car's lap and a slow car's lap both go into the same fit, leaving the model unable to distinguish 
"this lap is slow because the tyre is old" from "this lap is slow because it's a slower car".
Stints starting later in the race also sit on a faster, more rubbered-in track.

### Attempt 3 - give every stint its own baseline

I subtracted each stint's own average lap time from every lap in that stint. This centers each stint
on zero, so car pace and track position drop out and only the *shape* is left - which
is the degradation signal.

**Result:** HARD 0.0180, MEDIUM 0.0031. Both positive now, and much smaller but still the
wrong way round.

**Why it still failed:** the two compounds weren't being measured over the same part of their
lives. Checking the tyre age ranges at Monza:

| Compound | Laps | Youngest | Oldest | Mean age |
|----------|------|----------|--------|----------|
| HARD     | 672  | 1        | 41     | 13.7     |
| MEDIUM   | 236  | 1        | 19     | 7.5      |

Hard stints extend to 41 laps, deep into the part where a tyre wears significantly. Most medium stints
were pitted at 10–17 laps, while the tyre was still in its good phase. Degradation isn't a
straight line, it's flat-ish then steepens. Fitting a line to different portions of that
curve gives numbers that can't be compared.

### Attempt 4 - compare like with like

Restrict both compounds to a common tyre age window (3–15 laps) so I'm looking at the same
phase of tyre life for each.

**Result at Monza:**

| Compound | Laps counted | Slope (s per lap of tyre age) | R^2 |
|----------|--------------|-------------------------------|-----|
| MEDIUM   | 192          | 0.0256                        | 0.0338 |
| HARD     | 373          | 0.0150                        | 0.0085 |

Mediums degrade roughly 70% faster than hards. The fit was too weak to confirm this ordering

Importantly, none of these four attempts mistakenly questioned the data's accuracy, each was a factor
that needed careful consideration and removal.

---

## Validating on a second circuit

A model that only ever runs on one race can't be verified. So to test i. ran the **identical**
query on a known circuit for high degradation and check if the slopes increase. Barcelona (2024 Spanish GP) 
features long, sustained corners, high lateral loads, abrasive surface, the opposite of Monza.

I predicted  **slopes noticeably steeper than Monza, with the same compound ordering.**

I confirmed the race was clean first (no safety car, no rain, gentle downward trend which it was).

**Result, same 3–15 window, nothing else changed:**

| Circuit   | HARD   | MEDIUM |
|-----------|--------|--------|
| Monza     | 0.0150 | 0.0256 |
| Barcelona | 0.0659 | 0.0664 |

Barcelona comes out 2–4× steeper with the same methodology. **The circuit comparison validated.**
This is the critical finding, the model is tracking something real about how punishing a track is.

**However, The compound comparison did not validate.** At Barcelona hards and mediums were effectively
the same (0.0659 vs 0.0664), and softs had the *shallowest* slope, which is
unexpected. Widening the window to 3–20 moved hards from 0.0659 to 0.0382 - a 70% shift based on a minor
change to a parameter.

**If a conclusion changes when you adjust a parameter, its not a conclusion.** The compound
ordering at Monza may have been luck.

---

## Adding a confidence measure

The previous error was reading slopes to four decimal places without knowing how well
the data fit the line. `regr_slope` provides a number whether the data is a tight line or a
scattered.

`regr_r2` gives R² - the proportion of lap time variation that tyre age explains, 0 to 1.

**Barcelona, 3–15 window:**

| Compound | Laps | Slope  | R²     |
|----------|------|--------|--------|
| HARD     | 169  | 0.0659 | 0.0932 |
| MEDIUM   | 247  | 0.0664 | 0.2907 |
| SOFT     | 330  | 0.0509 | 0.0679 |

This explains everything that was confusing:

- **MEDIUM is the one trustworthy result.** R² 0.29 shows a decent fit for this data, and
  the slope barely changed across different variations I tested (0.0572 at window 3–20, 0.0664 at
  3–15, no change after filtering outliers). That consistency is what a real signal looks like.
- **HARD and SOFT aren't measurable this way on this data.** R² under 0.10 means tyre age
  explains less than a tenth of the variation, making the slopes mere estimates that significantly 
  fluctuate when parameters change.

The likely reason medium fits better: medium stints took place in the middle of the race at similar
lengths across drivers, aligning with the part of tyre life that shows a roughly linear. 
Softs ran short stints, while hards ran longer ones through the flat-to-steep transition where a 
straight line doesn't fit well.

**Reporting R² alongside the slope is now part of the model output, not an optional extra.**

---

## Outlier filtering (attempted, made no difference)

Hypothesis: unexplained variance comes from laps affected by traffic, lock-ups, or lifting for yellow
flags. The usual approach is to cut a percentage of laps, then discard any that are slower than 107% of the
stint's median. This 107% mirrors F1's own qualifying rule; at Barcelona it's about 5.7 seconds off.

**Result: 330 laps became 329.** One lap removed across the whole dataset. R² remained the same to three
decimal places.

**What that tells me:** there are almost no extremely slow laps in this data. The leftover
variance is ordinary racing scatter of 1–3%. This includes a compromised corner exit, a lap in dirty air,
all issues which a 107% filter sails straight over.

It's worth noting the single removed lap moved SOFT's slope from 0.0509 to 0.0403, a 20% swing from
just one data point. This highlights how fragile that fit is.

I deliberately chose **not** to keep tightening the threshold for a better R². Filtering until the
numbers look good, creates a fit rather than finding one. If I revisit this, I will select the
threshold before looking at the results, and report the number of laps removed.

---

## The final query

```sql
WITH stint_laps AS (
    SELECT s.driver_number,
           s.stint_number,
           s.compound,
           l.lap_duration,
           s.tyre_age_at_start + (l.lap_number - s.lap_start) AS tyre_age,
           l.lap_duration + 0.035 * l.lap_number              AS fuel_corrected
    FROM stint s
    JOIN lap l
      ON  l.session_key   = s.session_key
      AND l.driver_number = s.driver_number
      AND l.lap_number > s.lap_start
      AND l.lap_number < s.lap_end
    WHERE s.session_key = :sessionKey
      AND l.lap_duration IS NOT NULL
),
stint_stats AS (
    SELECT driver_number,
           stint_number,
           AVG(fuel_corrected)                                       AS stint_avg,
           PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY lap_duration) AS stint_median
    FROM stint_laps
    GROUP BY driver_number, stint_number
)
SELECT sl.compound,
       COUNT(*)                                                                     AS laps_counted,
       ROUND(regr_slope(sl.fuel_corrected - ss.stint_avg, sl.tyre_age)::numeric, 4) AS slope,
       ROUND(regr_r2(sl.fuel_corrected - ss.stint_avg, sl.tyre_age)::numeric, 4)    AS r2
FROM stint_laps sl
JOIN stint_stats ss
  ON  ss.driver_number = sl.driver_number
  AND ss.stint_number  = sl.stint_number
WHERE sl.tyre_age BETWEEN 3 AND 15
  AND sl.lap_duration <= 1.07 * ss.stint_median
GROUP BY sl.compound
ORDER BY sl.compound;
```

What each part is doing:

- **`stint_laps`** does the join and calculates the derived columns, giving one row per usable lap.
  `lap_number > lap_start` and `< lap_end` (strictly, not `BETWEEN`) is what excludes the out
  lap and the in lap.
- **`tyre_age`** is derived, not stored, it combines how old the tyre was when the stint started with
  the number of laps completed in the stint.
- **`fuel_corrected`** adds back the time the car gained from burning fuel.
- **`stint_stats`** reduces data to one row per stint that holds the average and the
  median used for outlier filtering.
- **The final query** joins each lap back to its stint's stats, subtracts the stint's own
  baseline so car pace and track position drop out, filters for a common tyre age window and
  drops outliers, then fits a line per compound.
- **`regr_slope(y, x)`** fits the least-squares line. The first argument is what's being predicted
  (lap time relative to the stint's own average), second is what it's predicted from (tyre age).
- **`regr_r2`** says how much of the variation that line actually explains.

Using CTEs (`WITH`) instead of nested subqueries allows each step to be clearly named and 
reasoned about on its own.

---

## What I can and can't currently claim

**Can claim:**

- Tyre degradation is measurable for mediums - Barcelona shows 0.066 s/lap with R² 0.29.
- The method correctly differentiates a high degradation circuit from a low one: Barcelona comes
  out 2–4× Monza using the same method and parameters.

**Cannot claim:**

- Reliable degradation figures for hard or soft compounds. R² below 0.10 and slopes unstable to
  window choice.
- Any specific compound *ordering*. The ranking changes between circuits and window settings.

---

## Limitations

I want to clarify these limitations because a number without its assumptions is meaningful:

- Results apply only for **tyre ages 3–15 only**, on **dry races**, at **two circuits**.
- The fuel coefficient of **0.035 s/lap is assumed, not measured**. It should be derived from
  the data or from fuel load estimates.
- **Track evolution is not corrected for.** The surface gets faster through a race, which biases
  anything measured late on.
- **Traffic is not detected.** This is probably the largest remaining source of noise,
  and the 107% filter showed it isn't manageable with a simple threshold.
- The fit is **linear**, but true degradation curves becomes steeper with age. A straight line is an
  approximation that gets less accurate with a wider age window.
- **R² is low for two of three compounds.** Those slopes should not be used downstream.
- Only **two circuits** validated. Two agreeing with expectation is encouraging, not proof.

---

## Next things to do

- Add a third circuit (Budapest, meeting 1241) to strengthen the circuit validation.
- Derive the fuel coefficient rather than assuming it.
- Traffic detection - needs gap reconstruction between cars, which is its own engine.
- Try a non-linear fit (degradation curves steepen; a straight line is the wrong shape).
- Handle the deferred contaminants: safety car detection, weather regime detection.
- Run the model against Silverstone (9558, wet-dry-wet) as a negative test - a well behaved
  model should produce obvious nonsense or refuse, and that's worth demonstrating.
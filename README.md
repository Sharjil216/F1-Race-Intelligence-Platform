[![CI](https://github.com/Sharjil216/F1-Race-Intelligence-Platform/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Sharjil216/F1-Race-Intelligence-Platform/actions/workflows/ci.yml)

# F1 Race Intelligence Platform 🏎️ 💫

The **F1 Race Intelligence Platform** is a java based race intelligence and strategy simulation platform 
that ingests public F1 telemetry/timing data, stores it in a time-series database, reconstructs race state,
models tyre degradation, and evaluates counterfactual strategy options using analytics and Monte Carlo
simulation, presenting it in a dashboard.

---

## What it does ✅

- Ingests meetings, sessions, drivers, laps, stints and pit stops from the OpenF1 API
- Keeps the raw API response alongside normalised tables, so any result can be traced back to source
- Reconstructs lap by lap running order and gaps from lap times alone
- Measures tyre degradation per compound, with fuel correction and per stint baselining
- Strategy simulations for one stop races, returning the optimal stop lap compared to the actual stop lap, and the time lost

---

## Example - Frontend 📸

![F1 Race Intelligence dashboard](docs/dashboard.png)

---

## Example - Backend 📸


```bash
curl "http://localhost:8080/api/analysis/race-state?sessionKey=9590&lap=30"
```

```json
[
  {"position":1,"driverNumber":81,"gapToLeader":0.000,"gapToAhead":null},
  {"position":2,"driverNumber":4,"gapToLeader":3.114,"gapToAhead":3.114},
  {"position":3,"driverNumber":16,"gapToLeader":4.873,"gapToAhead":1.759},
  {"position":4,"driverNumber":55,"gapToLeader":17.203,"gapToAhead":12.330},
  {"position":5,"driverNumber":44,"gapToLeader":18.551,"gapToAhead":1.348},
  {"position":6,"driverNumber":1,"gapToLeader":25.849,"gapToAhead":7.298},
  {"position":7,"driverNumber":11,"gapToLeader":29.987,"gapToAhead":4.138},
  {"position":8,"driverNumber":63,"gapToLeader":30.560,"gapToAhead":0.573},
  {"position":9,"driverNumber":14,"gapToLeader":40.790,"gapToAhead":10.230},
  {"position":10,"driverNumber":31,"gapToLeader":45.621,"gapToAhead":4.831},
  {"position":11,"driverNumber":23,"gapToLeader":46.788,"gapToAhead":1.167},
  {"position":12,"driverNumber":20,"gapToLeader":47.856,"gapToAhead":1.068},
  {"position":13,"driverNumber":3,"gapToLeader":53.093,"gapToAhead":5.237},
  {"position":14,"driverNumber":77,"gapToLeader":55.938,"gapToAhead":2.845},
  {"position":15,"driverNumber":43,"gapToLeader":56.750,"gapToAhead":0.812},
  {"position":16,"driverNumber":18,"gapToLeader":58.038,"gapToAhead":1.288},
  {"position":17,"driverNumber":10,"gapToLeader":61.918,"gapToAhead":3.880},
  {"position":18,"driverNumber":27,"gapToLeader":66.605,"gapToAhead":4.687},
  {"position":19,"driverNumber":24,"gapToLeader":72.502,"gapToAhead":5.897}
]
```

The endpoint returns an array or position results, containing a driver identifiable by their driver number,
their position on track, their gap to the leader (0.00s if they are the leader), and gap to the car 
ahead (null for the race leader).

---

## Quick start 💻

Requirements: JDK 21, Docker.

```bash
docker compose up -d
cd backend && ./gradlew bootRun
```

Then ingest a session (parents before children - the schema enforces it):

```bash
curl -X POST "http://localhost:8080/api/ingestion/meetings?year=2024"
curl -X POST "http://localhost:8080/api/ingestion/sessions?meetingKey=1244"
curl -X POST "http://localhost:8080/api/ingestion/drivers?sessionKey=9590"
curl -X POST "http://localhost:8080/api/ingestion/laps?sessionKey=9590"
curl -X POST "http://localhost:8080/api/ingestion/stints?sessionKey=9590"
curl -X POST "http://localhost:8080/api/ingestion/pits?sessionKey=9590"
```

API docs: <http://localhost:8080/swagger-ui.html>

---

## Architecture 🏗️

**Stack:** Java 21, Spring Boot, Spring Data JDBC, PostgreSQL + TimescaleDB, Flyway, Gradle, Docker, Testcontainers, WireMock.  
**Data Chain:** OpenF1 API -> raw JSONB snapshot + normalised tables -> analysis engines -> REST API.  
**Ingestion:** Client record, services, endpoints per entity  
**Engine:** SQL based analysis, exposed aas read endpoints  
**Flyway migration own the schema**


---

## Design decisions worth calling out 🧩

- Idempotent ingestion - writes use `INSERT ... ON CONFLICT`, so any endpoint can be re-run safely without duplicates or manual cleanup
- Raw payloads stored as JSONB alongside normalised rows, so a suspect value can be checked against exactly what the API returned on that call, without re-fetching.
  This choice was one that helped me track down and fix a bug where some fields were incorrectly mapped, due to their name containing `-` instead of `_`. [(see here)](https://github.com/Sharjil216/F1-Race-Intelligence-Platform/commit/697a7a5cbb690eec4dbf13d3e7081c4ccdfe0bf2])
- Spring Data JDBC over JPA as the workload is bulk inserts and analytical SQL, both of which an ORM gets in the way of.
- Integration tests run against a real PostgreSQL in a throwaway container, with the HTTP layer stubbed, so they catch driver and SQL syntax bugs but need no network
- Analysis returns its own confidence (R², sample counts) rather than a bare number

---

## Analysis: what it found 📊

Tyre degradation is measured per compound, correcting for fuel burn and baselining each
stint against its own average pace so that car performance and track position drop out.

- **Degradation is non-linear.** Binning laps by tyre age shows a steady climb to around
  15 laps, then divergence by compound - softs at Barcelona turn sharply upward after
  ~18 laps (a performance cliff), while hards plateau for longer.
- **The model separates circuits as expected.** Barcelona shows roughly 2.5× the
  degradation of Monza on the same compounds and identical methodology, consistent with
  their reputations as high and low degradation tracks.
- **Race state is reconstructed from lap times alone** - cumulative race time, running
  order, gap to the leader and gap to the car ahead, for every driver on every lap.
- **Results carry their own confidence.** Every degradation figure is returned with an
  R² and a lap count, because explanatory power is low for several compound/circuit
  combinations and a bare slope would overstate what the data supports.

Method, the approaches that failed, and what each one revealed:
[`docs/modelling-notes.md`](docs/modelling-notes.md)

**Strategy simulation** - composes the degradation, race state and pit loss engines to find 
the tyre optimal one stop lap, and compares it against what actually happened. On Leclerc's 
winning Monza one stop, the model's optimum lands within half a second of his real call, inside the 
uncertainty of the inputs.

---

## Limitations ❌

- **Dry races only.** Wet or mixed-condition sessions are not detected or excluded.
- **The fuel coefficient (0.035 s/lap) is assumed, not measured.** It doesn't affect
  compound comparison within a session, but it does affect comparison between circuits.
- **Track evolution and traffic are uncorrected.** Traffic filtering using reconstructed
  gaps was tested and removed up to 40% of laps for a marginal gain, so it isn't applied
  by default.
- **Fit quality is low for hard and soft compounds** (R² below 0.13). Those slopes should
  not be used downstream without treating them as indicative only.
- **Validated on two circuits.** Two results agreeing with expectation is encouraging,
  not proof.

---

## Status and roadmap 📍

**Working**

- Ingestion for meetings, sessions, drivers, laps, stints and pit stops, with raw
  payloads archived alongside normalised tables
- Race state reconstruction (position, gap to leader, gap to car ahead)
- Tyre degradation analysis - per-compound slope with R², and a binned degradation curve
- Integration tests against a real PostgreSQL container, running in CI
- Validate reconstructed positions against OpenF1's reported positions, to put a number
    on reconstruction accuracy
- Derive pit loss from lap-time deltas (the API's pit durations measure lane traversal,
  not time lost)
- Strategy counterfactuals - "what if this stop had happened three laps earlier?"

**Next**

- Telemetry ingestion and a frontend

Not yet built: anything predictive, weather handling, or the strategy simulator itself.

---

## Data source 

Timing data from the [OpenF1 API](https://openf1.org/). This project is not affiliated
with Formula 1 or any team.
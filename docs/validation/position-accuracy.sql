/*
 This SQL query calculates a drivers position based on their end of lap time and generates
 a track position based on a calculation. It then verifies this against api data.
 */

WITH lap_totals AS (
    SELECT l.lap_number,
           l.driver_number,
           l.lap_duration,
           l.date_start + (l.lap_duration * INTERVAL '1 second') AS lap_end_time
    FROM lap l
    WHERE session_key = 9590
),
     reconstructed AS (
         SELECT driver_number,
                lap_number,
                RANK() OVER (PARTITION BY lap_number ORDER BY lap_end_time) AS my_position
         FROM lap_totals
     )
SELECT r.driver_number, r.lap_number, r.my_position, reported.position
FROM lap l JOIN reconstructed r ON r.driver_number = l.driver_number AND r.lap_number = l.lap_number
           LEFT JOIN LATERAL (
    SELECT p.position FROM driver_position p
    WHERE p.session_key = 9590 AND p.driver_number = l.driver_number AND p.position_time <= l.date_start + (l.lap_duration * INTERVAL '1 second')
    ORDER BY p.position_time DESC LIMIT 1
        ) reported ON true
WHERE l.session_key = 9590 AND l.lap_number = 30 AND l.date_start IS NOT NULL;
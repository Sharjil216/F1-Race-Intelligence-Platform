import { useState, useEffect } from "react"
import { API_BASE } from "./config"

type RaceStateRow = {
    position: number
    driverNumber: number
    gapToLeader: number
    gapToAhead: number | null
}

type DriverInfo = {
    driverNumber: number
    fullName: string
    teamColour: string
    nameAcronym: string
}

function RaceStatePanel({ sessionKey }: { sessionKey: number }) {
    const [raceState, setRaceState] = useState<RaceStateRow[]>([])
    const [drivers, setDrivers] = useState<DriverInfo[]>([])
    const [lap, setLap] = useState(30)

    useEffect(() => {
        fetch(`${API_BASE}/api/drivers?sessionKey=${sessionKey}`).then(r => r.json()).then(json => {setDrivers(json)})
        fetch(`${API_BASE}/api/analysis/race-state?sessionKey=${sessionKey}&lap=${lap}`).then(r => r.json()).then(json => setRaceState(json))
    }, [sessionKey, lap])

    const driverLookup = new Map(drivers.map(d => [d.driverNumber, d]))



    return (
        <>
        <div style={{display: "flex", flexDirection: "column", gap: "4px"}}>
            <input
            type="range"
            min={1}
            max={66}
            value={lap}
            onChange={(e) => setLap(Number(e.target.value))}
            style={{accentColor: '#E10600'}}
            /> <span>{lap}</span>
        {raceState.map((row) => {
            const driver = driverLookup.get(row.driverNumber)
            return (
                <div key={row.driverNumber} style={{display: 'flex', alignItems: 'center', gap: '10px', height: '30px'}}>
                    <div style={{width: '30px'}}>{row.position}</div>
                    <div style={{ width: '5px', height: '30px', backgroundColor: `#${driver?.teamColour}`}}></div>
                    <div style={{width: '40px'}}>{driver?.nameAcronym}</div>
                    <div style={{width: '40px', textAlign: 'right', fontFamily: 'monospace'}}>{row.gapToAhead === null ? 'LEADER' : '+' + row.gapToAhead + 's'}</div>
                </div>
            )
        })}
        </div>
        </>
    )
}

export default RaceStatePanel
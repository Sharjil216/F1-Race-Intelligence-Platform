import { useState, useEffect } from "react"

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

function RaceStatePanel() {
    const [raceState, setRaceState] = useState<RaceStateRow[]>([])
    const [drivers, setDrivers] = useState<DriverInfo[]>([])

    useEffect(() => {
        fetch('http://localhost:8080/api/drivers?sessionKey=9539').then(r => r.json()).then(json => {setDrivers(json)})
        fetch('http://localhost:8080/api/analysis/race-state?sessionKey=9539&lap=30').then(r => r.json()).then(json => setRaceState(json))
    }, [])

    const driverLookup = new Map(drivers.map(d => [d.driverNumber, d]))



    return (
        <div style={{display: "flex", flexDirection: "column", gap: "4px"}}>
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
    )
}

export default RaceStatePanel
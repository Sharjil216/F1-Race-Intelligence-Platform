import { useEffect, useState } from "react"
type Stint = {
    compound: string
    startLap: number
    endLap: number
}

type OptimiserResult = {
    cost: number
    stints: Stint[]
}

type MultiStopStrategyRow = {
    costOfStrategy: number
    matchedOptimal: OptimiserResult
    bestOverall: OptimiserResult
    worstR2: number
}

type DriverInfo = {
    driverNumber: number
    fullName: string
    teamColour: string
    nameAcronym: string
}

function MultiStopStrategyPanel({ sessionKey }: { sessionKey: number }) {
    const [multiStopRow, setMultiStopRow] = useState<MultiStopStrategyRow | null>(null)
    const [drivers, setDrivers] = useState<DriverInfo[]>([])
    const [driverToLookup, setDriverToLookup] = useState(16)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        fetch(`http://localhost:8080/api/drivers?sessionKey=${sessionKey}`)
        .then(r => r.json()).then(json => setDrivers(json))
    }, [sessionKey])

    useEffect(() => {
        setLoading(true)
        fetch(`http://localhost:8080/api/analysis/multi-stop-strategy?sessionKey=${sessionKey}&driverNumber=${driverToLookup}`).then(r => r.json()).then(json => {
            setMultiStopRow(json)
            setLoading(false)
        })
    }, [sessionKey, driverToLookup])

    const driverLookup = new Map(drivers.map(d => [d.driverNumber, d]))

    function changeDriver(e) {
        setDriverToLookup(Number(e.target.value))
    }

    const driverSelector = (
        <div>
        <h3>Driver Selector</h3>
        <select value={driverToLookup} onChange={changeDriver}>
            {drivers.map((d) => (
            <option key={d.driverNumber} value={d.driverNumber}>
                {d.fullName} - {d.driverNumber}
            </option>
            ))}
        </select>
        </div>
    )

    if (loading) {
        return <div>{driverSelector}<div style={{ color: '#666' }}>Loading…</div></div>
    }

    if (!multiStopRow) return <div style={{ color: '#888' }}>No multi-stop analysis available for this driver.</div>

    const driver = driverLookup.get(driverToLookup)
    const confidenceColour = multiStopRow.worstR2 < 0.1 ? '#E10600' : multiStopRow.worstR2 < 0.3 ? '#FFD12E' : '#52e252'
    const confidence = multiStopRow.worstR2 < 0.1 ? 'Low' : multiStopRow.worstR2 < 0.3 ? 'Moderate' : 'High'

    const tyreColour: Record<string, string> = {
        SOFT: '#DA291C', MEDIUM: '#FFD12E', HARD: '#F0F0EC'
    }

    const renderStints = (stints: Stint[]) => (
        <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap', marginTop: 6 }}>
            {stints.map((s) => (
                <span key={s.startLap} style={{
                    fontFamily: 'monospace', fontSize: '0.8rem',
                    borderLeft: `3px solid ${tyreColour[s.compound] ?? '#888'}`,
                    paddingLeft: 6, color: '#ccc'
                }}>
                    {s.compound} {s.startLap}–{s.endLap}
                </span>
            ))}
        </div>
    )

    return (
    <div>
        {driverSelector}
        <div style={{ fontSize: '0.8rem', color: '#888', marginBottom: 2 }}>
            MULTI-STOP - {driver.fullName} ({driver.driverNumber})
        </div>

        <div style={{ fontSize: '0.85rem', color: '#aaa', marginBottom: 16 }}>
            Actual strategy cost <span style={{ fontFamily: 'monospace', color: '#F0F0EC' }}>
                {multiStopRow.costOfStrategy.toFixed(3)}s
            </span>
        </div>

        <div style={{ marginBottom: 16 }}>
            <div style={{ fontSize: '0.8rem', color: '#888', textTransform: 'uppercase' }}>
                Optimal - same stop count
            </div>
            <div style={{ fontSize: '1.4rem', fontWeight: 600, fontFamily: 'monospace' }}>
                {multiStopRow.matchedOptimal.cost.toFixed(3)}s
            </div>
            {renderStints(multiStopRow.matchedOptimal.stints)}
        </div>

        <div style={{ marginBottom: 16 }}>
            <div style={{ fontSize: '0.8rem', color: '#888', textTransform: 'uppercase' }}>
                Optimal - any stop count
            </div>
            <div style={{ fontSize: '1.4rem', fontWeight: 600, fontFamily: 'monospace' }}>
                {multiStopRow.bestOverall.cost.toFixed(3)}s
            </div>
            {renderStints(multiStopRow.bestOverall.stints)}
            </div>

            <div style={{ borderLeft: `3px solid ${confidenceColour}`, paddingLeft: 12 }}>
            <span style={{ color: confidenceColour }}>{confidence} confidence</span>
            <span style={{ fontSize: '0.8rem', color: '#888' }}> - degradation model R² {multiStopRow.worstR2.toFixed(3)}</span>
        </div>
    </div>
    )
}

export default MultiStopStrategyPanel
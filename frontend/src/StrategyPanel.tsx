import { useState, useEffect } from "react"
import { API_BASE } from "./config"

type StrategyResult = {
    driverNumber: number
    totalLaps: number
    actualStopLap: number
    optimalStopLap: number
    actualCost: number,
    optimalCost: number,
    timeDeltaSeconds: number,
    firstCompound: string,
    secondCompound: string,
    firstR2: number,
    secondR2: number
}

type DriverInfo = {
    driverNumber: number
    fullName: string
    teamColour: string
    nameAcronym: string
}

function StrategyPanel({ sessionKey }: { sessionKey: number }) {
  const [strategy, setStrategy] = useState<StrategyResult | null>(null)
  const [drivers, setDrivers] = useState<DriverInfo[]>([])
  const [driverToLookup, setDriverToLookup] = useState(16)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetch(`${API_BASE}/api/drivers?sessionKey=${sessionKey}`)
      .then(r => r.json()).then(json => setDrivers(json))
  }, [sessionKey])

  useEffect(() => {
    setLoading(true)
    fetch(`${API_BASE}/api/analysis/strategy?sessionKey=${sessionKey}&driverNumber=${driverToLookup}`)
      .then(r => r.ok ? r.json() : null)
      .then(json => {
        setStrategy(json)
        setLoading(false)
      })
  }, [sessionKey, driverToLookup])

  const driverLookup = new Map(drivers.map(d => [d.driverNumber, d]))

  function changeDriver(e: React.ChangeEvent<HTMLSelectElement>) {
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

  if (strategy === null) {
    return (
      <div>
        {driverSelector}
        <div style={{ color: '#888' }}>
          No one stop strategy available for this driver, the model currently supports one stop races only.
        </div>
      </div>
    )
  }

  const driver = driverLookup.get(strategy.driverNumber)
  const worstR2 = Math.min(strategy.firstR2, strategy.secondR2)
  const confidence = worstR2 < 0.1 ? 'Low' : worstR2 < 0.3 ? 'Moderate' : 'High'
  const confidenceColour = worstR2 < 0.1 ? '#E10600' : worstR2 < 0.3 ? '#FFD12E' : '#52e252'

  return (
    <div>
        {driverSelector}
        <div style={{ fontSize: '0.8rem', color: '#888', marginBottom: 4 }}>
            STRATEGY - {driver?.fullName ?? `#${strategy.driverNumber}`}
        </div>

        <div style={{ fontSize: '1.6rem', fontWeight: 600, marginBottom: 4 }}>
            Optimal stop: lap {strategy.optimalStopLap}
        </div>
        <div style={{ fontSize: '0.95rem', color: '#aaa', marginBottom: 16 }}>
            Actual stop: lap {strategy.actualStopLap}
            {' - '}
            {strategy.firstCompound} → {strategy.secondCompound}
        </div>

        <div style={{
            borderLeft: `3px solid ${confidenceColour}`,
            paddingLeft: 12, marginBottom: 16
        }}>
            <div style={{ fontFamily: 'monospace', fontSize: '1.1rem' }}>
            Δ {strategy.timeDeltaSeconds.toFixed(3)}s
            </div>
            <div style={{ fontSize: '0.8rem', color: '#888' }}>
            <span style={{ color: confidenceColour }}>{confidence} confidence</span>
            {' - '}within model uncertainty (R² {strategy.firstR2.toFixed(2)} / {strategy.secondR2.toFixed(2)})
            </div>
        </div>

        <div style={{ fontSize: '0.8rem', color: '#666', lineHeight: 1.6 }}>
            <div>Race distance: {strategy.totalLaps} laps</div>
            <div>Tyre cost - actual {strategy.actualCost.toFixed(3)}s - optimal {strategy.optimalCost.toFixed(3)}s</div>
        </div>
    </div>
  )
}

export default StrategyPanel
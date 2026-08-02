import { useState, useEffect } from "react"

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

function StrategyPanel() {
  const [strategy, setStrategy] = useState<StrategyResult | null>(null)
  const [drivers, setDrivers] = useState<DriverInfo[]>([])

  useEffect(() => {
    fetch('http://localhost:8080/api/drivers?sessionKey=9539')
      .then(r => r.json()).then(json => setDrivers(json))
    fetch('http://localhost:8080/api/analysis/strategy?sessionKey=9590&driverNumber=16')
      .then(r => r.json()).then(json => setStrategy(json))
  }, [])

  const driverLookup = new Map(drivers.map(d => [d.driverNumber, d]))

  if (!strategy) return <div>Loading…</div>

  const driver = driverLookup.get(strategy.driverNumber)
  const worstR2 = Math.min(strategy.firstR2, strategy.secondR2)
  const confidence = worstR2 < 0.1 ? 'Low' : worstR2 < 0.3 ? 'Moderate' : 'High'
  const confidenceColour = worstR2 < 0.1 ? '#E10600' : worstR2 < 0.3 ? '#FFD12E' : '#52e252'

  return (
    <div>
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
        <div>Tyre cost - actual {strategy.actualCost.toFixed(3)}s · optimal {strategy.optimalCost.toFixed(3)}s</div>
      </div>
    </div>
  )
}

export default StrategyPanel
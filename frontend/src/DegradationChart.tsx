import { useState, useEffect } from 'react'
import {
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer
} from 'recharts'

type CurvePoint = {
  compound: string
  ageBucket: number
  laps: number
  avgDelta: number
}

type ChartRow = {
  ageBucket: number
  [compound: string]: number
}

function reshapeForChart(flat: CurvePoint[]) {
  const byBucket = new Map<number, ChartRow>()

  for (const row of flat) {
    if (!byBucket.has(row.ageBucket)) {
      byBucket.set(row.ageBucket, { ageBucket: row.ageBucket })
    }
    const entry = byBucket.get(row.ageBucket)!
    entry[row.compound] = row.avgDelta
  }

  return Array.from(byBucket.values())
    .sort((a, b) => a.ageBucket - b.ageBucket)
}

function DegradationChart() {
    const [chartData, setChartData] = useState<ChartRow[]>([])

  useEffect(() => {
    fetch('http://localhost:8080/api/analysis/degradation-curve?sessionKey=9539')
        .then((r) => r.json())
        .then((json) => setChartData(reshapeForChart(json)))
    }, [])

    return (
    <div style={{ height: 400 }}>

    <ResponsiveContainer>
        <LineChart data={chartData} margin={{ top: 20, right: 40, bottom: 40, left: 20 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#1E1E28" />
          <XAxis dataKey="ageBucket" stroke="#555" tick={{ fill: '#888' }}
                 label={{ value: 'Tyre age (laps)', position: 'bottom', fill: '#888' }} />
          <YAxis stroke="#555" tick={{ fill: '#888' }} />
          <Tooltip contentStyle={{ background: '#15151E', border: '1px solid #2A2A38', borderRadius: 4 }}
                   labelStyle={{ color: '#F0F0EC' }} />
          <Legend verticalAlign="top" height={36} />
          <Line type="monotone" dataKey="SOFT" stroke="#DA291C" strokeWidth={2.5} dot={true} />
          <Line type="monotone" dataKey="MEDIUM" stroke="#FFD12E" strokeWidth={2.5} dot={true} />
          <Line type="monotone" dataKey="HARD" stroke="#F0F0EC" strokeWidth={2.5} dot={true} />
        </LineChart>
    </ResponsiveContainer>
    </div>
    )
}

export default DegradationChart
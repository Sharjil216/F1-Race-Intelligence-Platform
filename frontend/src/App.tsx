import { useState } from "react"
import DegradationChart from "./DegradationChart"
import RaceStatePanel from "./RaceStatePanel"
import StrategyPanel from "./StrategyPanel"
import MultiStopStrategyPanel from "./MultiStopStrategyPanel"

const panelStyle = {
  border: '1px solid #2A2A38',
  borderRadius: 8,
  padding: 20,
  background: '#12121A'
}

function App() {
    const [session, setSession] = useState(9590)
    function changeSession(e: React.ChangeEvent<HTMLSelectElement>) {
        setSession(Number(e.target.value))
    }


  return (
  <div style={{
    background: '#0A0A0F', minHeight: '100vh', color: '#F0F0EC',
    fontFamily: 'system-ui, sans-serif', padding: 32
  }}>
    <h1 style={{ fontWeight: 600, letterSpacing: '-0.02em' }}>
      <span style={{ color: '#E10600' }}>▎</span> F1 Race Intelligence - 2024
    </h1>
    <main>
        <div style={panelStyle}>
            <h3>Session Select</h3>
            <select name="sessions" id="sessions" onChange={changeSession}>
                <option value="9590">Monza - 9590</option>
                <option value="9539">Barcelona - 9539</option>
            </select>
        </div>
        <div style={panelStyle}>
            Degradation Chart
            <DegradationChart sessionKey={session} />
        </div>
        <div style={{display: 'flex', gap: '16px', marginTop: '10px'}}>
            <div style={{...panelStyle, flex: 1}}>
                Race State Gaps
                <RaceStatePanel sessionKey={session} />
            </div>
            <div style={{...panelStyle, flex: 1}}>
                <StrategyPanel sessionKey={session} />
            </div>
         </div>

         <div style={panelStyle}>
            <MultiStopStrategyPanel  sessionKey={session}/>
        </div>
        
    </main>
  </div>
    )
}

export default App
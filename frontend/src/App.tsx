import DegradationChart from "./DegradationChart"
import RaceStatePanel from "./RaceStatePanel"

const panelStyle = {
  border: '1px solid #2A2A38',
  borderRadius: 8,
  padding: 20,
  background: '#12121A'
}

function App() {

  return (
  <div style={{
    background: '#0A0A0F', minHeight: '100vh', color: '#F0F0EC',
    fontFamily: 'system-ui, sans-serif', padding: 32
  }}>
    <h1 style={{ fontWeight: 600, letterSpacing: '-0.02em' }}>
      <span style={{ color: '#E10600' }}>▎</span> F1 Race Intelligence - Barcelona 2024
    </h1>
    <main>
        <div style={panelStyle}>
            Degradation Chart
            <DegradationChart />
        </div>
        <div style={{display: 'flex', gap: '16px', marginTop: '10px'}}>
            <div style={{...panelStyle, flex: 1}}>
                Race State Gaps
                <RaceStatePanel />
            </div>
            <div style={{...panelStyle, flex: 1}}>
                Strategy
            </div>
         </div>
        

    </main>
  </div>
    )
}

export default App
// =============================================================================
// 04-component-lifecycle-in-functional-components.jsx
// =============================================================================
// React class components have explicit lifecycle methods:
//   componentDidMount, componentDidUpdate, componentWillUnmount
//
// Functional components don't have those methods — useEffect REPLACES all of
// them, and is more composable because you can have multiple effects.
//
// SECTIONS:
//  1. Class component with all lifecycle methods (reference)
//  2. Functional equivalent using useEffect
//  3. Lifecycle phase demos (mount / update / unmount)
//  4. Side-by-side comparison
//  5. Gotchas and ordering
// =============================================================================

import React, { Component, useState, useEffect, useRef } from 'react';

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1 — Class Component (for comparison only — don't teach this pattern)
// ─────────────────────────────────────────────────────────────────────────────
// This is the "old way". Shown here so students recognize it in legacy codebases.

export class LifecycleClassComponent extends Component {
  constructor(props) {
    super(props);
    this.state = { count: 0, data: null };
    console.log('1. constructor — component instance created');
  }

  // Runs ONCE after the component is added to the DOM
  componentDidMount() {
    console.log('2. componentDidMount — fetch data, set up subscriptions');
    // Simulate fetch
    setTimeout(() => {
      this.setState({ data: 'Loaded!' });
    }, 1000);
  }

  // Runs after EVERY update (when state or props change)
  componentDidUpdate(prevProps, prevState) {
    if (prevState.count !== this.state.count) {
      console.log(`3. componentDidUpdate — count changed to ${this.state.count}`);
      document.title = `Count: ${this.state.count}`;
    }
  }

  // Runs just BEFORE the component is removed from the DOM
  componentWillUnmount() {
    console.log('4. componentWillUnmount — cancel timers, remove subscriptions');
  }

  render() {
    const { count, data } = this.state;
    return (
      <div>
        <p>Count: {count} | Data: {data ?? 'loading…'}</p>
        <button onClick={() => this.setState({ count: count + 1 })}>+</button>
      </div>
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2 — Functional Equivalent Using useEffect
// ─────────────────────────────────────────────────────────────────────────────
// Three useEffect calls replace three lifecycle methods.
// Each effect handles ONE concern — more composable than class lifecycle.

export function LifecycleFunctionalComponent() {
  const [count, setCount] = useState(0);
  const [data, setData]   = useState(null);

  // ── MOUNT — runs once after first render (empty dependency array)
  //    Replaces: componentDidMount
  useEffect(() => {
    console.log('componentDidMount equivalent — runs once after mount');
    const timer = setTimeout(() => setData('Loaded!'), 1000);

    // ── UNMOUNT — the cleanup function runs before component is removed
    //    Replaces: componentWillUnmount
    return () => {
      console.log('componentWillUnmount equivalent — cleanup on unmount');
      clearTimeout(timer);
    };
  }, []);  // empty array → mount + unmount only

  // ── UPDATE (specific dep) — runs when `count` changes
  //    Replaces: componentDidUpdate with a prevState comparison
  useEffect(() => {
    console.log(`componentDidUpdate equivalent — count changed to ${count}`);
    document.title = `Count: ${count}`;
    // No cleanup needed here
  }, [count]);  // [count] → run whenever count changes

  // ── UPDATE (any render) — runs after every render
  //    Replaces: componentDidUpdate with no condition
  useEffect(() => {
    console.log('Runs after every render (no dep array)');
  });  // no array → every render

  return (
    <div>
      <p>Count: {count} | Data: {data ?? 'loading…'}</p>
      <button onClick={() => setCount(c => c + 1)}>+</button>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3 — Mount / Update / Unmount Demo with Visible Logs
// ─────────────────────────────────────────────────────────────────────────────

function InnerComponent({ courseId }) {
  const [log, setLog] = useState([]);

  const addLog = (msg) => setLog(prev => [...prev, `${new Date().toLocaleTimeString()} — ${msg}`]);

  // MOUNT + UNMOUNT
  useEffect(() => {
    addLog(`🟢 MOUNTED with courseId=${courseId}`);
    return () => addLog(`🔴 UNMOUNTED (courseId was ${courseId})`);
  }, []);

  // UPDATE — fires when courseId prop changes
  useEffect(() => {
    addLog(`🔄 courseId changed → ${courseId}`);
  }, [courseId]);

  return (
    <div className="log-box">
      <strong>Lifecycle Log:</strong>
      {log.map((entry, i) => <p key={i}>{entry}</p>)}
    </div>
  );
}

export function MountUpdateUnmountDemo() {
  const [mounted,  setMounted]  = useState(true);
  const [courseId, setCourseId] = useState(1);

  return (
    <div>
      <h2>Mount / Update / Unmount Demo</h2>
      <p>Watch the log panel inside the component to see lifecycle events fire.</p>

      <button onClick={() => setMounted(m => !m)}>
        {mounted ? '🔴 Unmount' : '🟢 Mount'} Component
      </button>
      <button onClick={() => setCourseId(id => id + 1)} disabled={!mounted}>
        ▶ Next Course (id={courseId})
      </button>

      {mounted && <InnerComponent courseId={courseId} />}
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4 — Side-by-Side Mapping Table (rendered as JSX)
// ─────────────────────────────────────────────────────────────────────────────

export function LifecycleMappingTable() {
  const rows = [
    {
      phase:     'MOUNT',
      classMethod: 'componentDidMount()',
      hook:      'useEffect(() => { … }, [])',
      notes:     'Runs once after the component is inserted into the DOM. Use for initial data fetching, subscriptions, DOM measurements.'
    },
    {
      phase:     'UPDATE',
      classMethod: 'componentDidUpdate(prevProps, prevState)',
      hook:      'useEffect(() => { … }, [dep1, dep2])',
      notes:     'Runs when listed dependencies change. The hook replaces the if(prevState.count !== count) pattern automatically.'
    },
    {
      phase:     'UPDATE (any)',
      classMethod: 'componentDidUpdate() — no condition',
      hook:      'useEffect(() => { … })  // no array',
      notes:     'Runs after every render. Rarely what you want — almost always include a dep array.'
    },
    {
      phase:     'UNMOUNT',
      classMethod: 'componentWillUnmount()',
      hook:      'useEffect(() => { return () => cleanup(); }, [])',
      notes:     'The function returned from useEffect is called right before unmount. Also called before re-running an effect when deps change.'
    },
    {
      phase:     'INITIAL STATE',
      classMethod: 'constructor() + this.state = {}',
      hook:      'useState(initialValue)',
      notes:     'Constructor initializes state once. useState lazy initializer: useState(() => expensiveCalc()) also runs once.'
    },
    {
      phase:     'DERIVED STATE',
      classMethod: 'static getDerivedStateFromProps()',
      hook:      'No direct equivalent — compute during render or use useMemo',
      notes:     'Calculate values from props/state directly in the function body, or memoize with useMemo.'
    },
  ];

  return (
    <div>
      <h2>Lifecycle Mapping: Class → Hooks</h2>
      <table border="1" cellPadding="8" style={{ borderCollapse: 'collapse', width: '100%' }}>
        <thead>
          <tr>
            <th>Phase</th>
            <th>Class Component</th>
            <th>Functional (Hook)</th>
            <th>Notes</th>
          </tr>
        </thead>
        <tbody>
          {rows.map(r => (
            <tr key={r.phase}>
              <td><strong>{r.phase}</strong></td>
              <td><code>{r.classMethod}</code></td>
              <td><code>{r.hook}</code></td>
              <td>{r.notes}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5 — Cleanup Deep Dive
// ─────────────────────────────────────────────────────────────────────────────
// The cleanup function in useEffect runs in TWO situations, not just unmount:
//   1. Before the component is unmounted
//   2. Before the effect re-runs due to a dependency change
//
// This is MORE correct than componentWillUnmount because it cleans up stale
// subscriptions each time dependencies change.

export function SubscriptionDemo() {
  const [channel, setChannel] = useState('general');
  const [messages, setMessages] = useState([]);

  useEffect(() => {
    // Simulate subscribing to a chat channel
    console.log(`✅ Subscribed to #${channel}`);
    const timer = setInterval(() => {
      setMessages(prev => [...prev.slice(-4), `[${channel}] message at ${Date.now()}`]);
    }, 1500);

    // Cleanup runs:
    //  • When `channel` changes (before re-subscribing to the new channel)
    //  • When the component unmounts
    return () => {
      console.log(`❌ Unsubscribed from #${channel}`);
      clearInterval(timer);
    };
  }, [channel]);  // re-runs whenever channel changes

  return (
    <div>
      <h2>Cleanup Runs on Dep Change Too</h2>
      <p>Change the channel — watch the console for unsubscribe → resubscribe.</p>
      {['general', 'react', 'jobs'].map(ch => (
        <button key={ch} onClick={() => { setChannel(ch); setMessages([]); }}
          style={{ fontWeight: ch === channel ? 'bold' : 'normal', marginRight: 8 }}>
          #{ch}
        </button>
      ))}
      <ul>
        {messages.map((m, i) => <li key={i}>{m}</li>)}
      </ul>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Root export
// ─────────────────────────────────────────────────────────────────────────────
export default function ComponentLifecycleDemo() {
  return (
    <div>
      <h1>Component Lifecycle in Functional Components</h1>
      <LifecycleMappingTable />
      <hr />
      <MountUpdateUnmountDemo />
      <hr />
      <LifecycleFunctionalComponent />
      <hr />
      <SubscriptionDemo />
    </div>
  );
}

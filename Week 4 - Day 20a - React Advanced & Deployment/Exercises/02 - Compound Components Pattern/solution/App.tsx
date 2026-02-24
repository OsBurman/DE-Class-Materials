import React, { useState, useContext, createContext } from 'react';

// ─── Context ──────────────────────────────────────────────────────────────────
interface TabsContextType {
  activeTab: string;
  setActiveTab: (id: string) => void;
}

// Default is a no-op object — meaningful only inside a provider.
const TabsContext = createContext<TabsContextType>({
  activeTab: '',
  setActiveTab: () => {},
});

// ─── Tabs (parent) ────────────────────────────────────────────────────────────
interface TabsProps {
  defaultTab: string;
  children: React.ReactNode;
}

function Tabs({ defaultTab, children }: TabsProps) {
  const [activeTab, setActiveTab] = useState(defaultTab);

  return (
    <TabsContext.Provider value={{ activeTab, setActiveTab }}>
      {children}
    </TabsContext.Provider>
  );
}

// ─── Tabs.List ────────────────────────────────────────────────────────────────
function TabsList({ children }: { children: React.ReactNode }) {
  return (
    <div style={{ display: 'flex', gap: '1rem', marginBottom: '1rem' }}>
      {children}
    </div>
  );
}

// ─── Tabs.Tab ─────────────────────────────────────────────────────────────────
function TabsTab({ id, children }: { id: string; children: React.ReactNode }) {
  const { activeTab, setActiveTab } = useContext(TabsContext);
  const isActive = activeTab === id;

  return (
    <button
      onClick={() => setActiveTab(id)}
      style={{
        padding: '0.4rem 1rem',
        cursor: 'pointer',
        border: 'none',
        background: 'transparent',
        fontWeight: isActive ? 'bold' : 'normal',
        borderBottom: isActive ? '2px solid blue' : '2px solid transparent',
      }}
    >
      {children}
    </button>
  );
}

// ─── Tabs.Panel ───────────────────────────────────────────────────────────────
// Only renders when its id matches the active tab.
function TabsPanel({ id, children }: { id: string; children: React.ReactNode }) {
  const { activeTab } = useContext(TabsContext);
  if (activeTab !== id) return null;
  return <div style={{ padding: '1rem', border: '1px solid #ddd' }}>{children}</div>;
}

// ─── Attach sub-components as static properties ───────────────────────────────
Tabs.List  = TabsList;
Tabs.Tab   = TabsTab;
Tabs.Panel = TabsPanel;

// ─── App ──────────────────────────────────────────────────────────────────────
export default function App() {
  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif' }}>
      <h1>Compound Components — Tabs</h1>
      <Tabs defaultTab="a">
        <Tabs.List>
          <Tabs.Tab id="a">Tab A</Tabs.Tab>
          <Tabs.Tab id="b">Tab B</Tabs.Tab>
          <Tabs.Tab id="c">Tab C</Tabs.Tab>
        </Tabs.List>
        <Tabs.Panel id="a">Content for Tab A — Introduction</Tabs.Panel>
        <Tabs.Panel id="b">Content for Tab B — Details</Tabs.Panel>
        <Tabs.Panel id="c">Content for Tab C — Summary</Tabs.Panel>
      </Tabs>
    </div>
  );
}

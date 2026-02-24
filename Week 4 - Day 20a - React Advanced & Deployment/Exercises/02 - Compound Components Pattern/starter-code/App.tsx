import React, { useState, useContext, createContext } from 'react';

// ─── Context ──────────────────────────────────────────────────────────────────
interface TabsContextType {
  // TODO: declare activeTab: string
  // TODO: declare setActiveTab: (id: string) => void
}

// TODO: create TabsContext with createContext<TabsContextType> and a sensible default
const TabsContext = createContext<TabsContextType>({} as TabsContextType);

// ─── Tabs (parent) ────────────────────────────────────────────────────────────
interface TabsProps {
  defaultTab: string;
  children: React.ReactNode;
}

function Tabs({ defaultTab, children }: TabsProps) {
  // TODO: declare activeTab state initialised to defaultTab

  return (
    // TODO: wrap children in <TabsContext.Provider value={{ activeTab, setActiveTab }}>
    <>{children}</>
  );
}

// ─── Tabs.List ────────────────────────────────────────────────────────────────
function TabsList({ children }: { children: React.ReactNode }) {
  // TODO: render a <div style={{ display:'flex', gap:'1rem', marginBottom:'1rem' }}> wrapping children
  return <>{children}</>;
}

// ─── Tabs.Tab ─────────────────────────────────────────────────────────────────
function TabsTab({ id, children }: { id: string; children: React.ReactNode }) {
  // TODO: read activeTab and setActiveTab from TabsContext using useContext
  // TODO: compute isActive = activeTab === id

  return (
    // TODO: render a <button> that calls setActiveTab(id) on click
    //       and applies fontWeight:'bold', borderBottom:'2px solid blue'
    //       when isActive is true
    <button>{children}</button>
  );
}

// ─── Tabs.Panel ───────────────────────────────────────────────────────────────
function TabsPanel({ id, children }: { id: string; children: React.ReactNode }) {
  // TODO: read activeTab from TabsContext
  // TODO: return null when activeTab !== id
  return <div>{children}</div>;
}

// ─── Attach sub-components ────────────────────────────────────────────────────
// TODO: Tabs.List  = TabsList
// TODO: Tabs.Tab   = TabsTab
// TODO: Tabs.Panel = TabsPanel

// ─── App ──────────────────────────────────────────────────────────────────────
export default function App() {
  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif' }}>
      <h1>Compound Components — Tabs</h1>
      {/* TODO: render <Tabs defaultTab="a"> with Tabs.List, three Tabs.Tab, and three Tabs.Panel */}
    </div>
  );
}

import { createContext, useContext, useState } from 'react';

// TODO Task 6: Implement compound component pattern for TabPanel
// TabPanel internally manages "activeTab" state via context
// Sub-components: TabPanel.Tab, TabPanel.Content

const TabContext = createContext(null);

// TODO Task 6: Build TabPanel as a compound component
// - Creates TabContext with { activeTab, setActiveTab }
// - Renders {children}
function TabPanel({ children, defaultTab = 0 }) {
  const [activeTab, setActiveTab] = useState(defaultTab);
  return (
    <TabContext.Provider value={{ activeTab, setActiveTab }}>
      <div className="tab-panel">{children}</div>
    </TabContext.Provider>
  );
}

// TODO Task 6: TabPanel.Tab — shows a button. Active when index === activeTab.
// Prop: id (number or string). On click, setActiveTab(id).
TabPanel.Tab = function Tab({ id, children }) {
  const { activeTab, setActiveTab } = useContext(TabContext);
  return (
    <button
      className={`tab-btn ${activeTab === id ? 'active' : ''}`}
      onClick={() => setActiveTab(id)}
    >
      {children}
    </button>
  );
};

// TODO Task 6: TabPanel.Content — renders children only when id === activeTab
TabPanel.Content = function Content({ id, children }) {
  const { activeTab } = useContext(TabContext);
  if (activeTab !== id) return null;
  return <div className="tab-content">{children}</div>;
};

export default TabPanel;

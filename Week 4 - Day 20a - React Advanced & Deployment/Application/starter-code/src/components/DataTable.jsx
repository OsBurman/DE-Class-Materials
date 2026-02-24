import { useState } from 'react';
import { useDataFilter } from '../hooks/useDataFilter.js';

// TODO Task 3: DataTable uses useDataFilter hook for filtered + sorted results
export default function DataTable({ data }) {
  const [sortKey, setSortKey] = useState('name');
  const [sortDir, setSortDir] = useState('asc');
  const [filters, setFilters] = useState({ dept: '', minSalary: '' });

  // TODO Task 3: Get filteredData and handleFilterChange from useDataFilter hook
  // const { filteredData, handleFilterChange } = useDataFilter(data, filters);
  // TODO: Also useMemo for sorted result inside the hook

  function toggleSort(key) {
    if (sortKey === key) setSortDir(d => d === 'asc' ? 'desc' : 'asc');
    else { setSortKey(key); setSortDir('asc'); }
  }

  return (
    <div className="data-table-container">
      <div className="table-filters">
        {/* TODO: Filter inputs — on change call handleFilterChange('dept', value) */}
        <input
          placeholder="Filter by dept"
          onChange={e => setFilters(prev => ({ ...prev, dept: e.target.value }))}
        />
        <input
          placeholder="Min salary"
          type="number"
          onChange={e => setFilters(prev => ({ ...prev, minSalary: e.target.value }))}
        />
      </div>

      <table className="data-table">
        <thead>
          <tr>
            {['name', 'dept', 'salary', 'startYear'].map(col => (
              <th key={col} onClick={() => toggleSort(col)} className="sortable">
                {col} {sortKey === col ? (sortDir === 'asc' ? '↑' : '↓') : ''}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {/* TODO Task 3: Map filteredData (sorted) to table rows */}
          {data.map(emp => (
            <tr key={emp.id}>
              <td>{emp.name}</td>
              <td>{emp.dept}</td>
              <td>${emp.salary.toLocaleString()}</td>
              <td>{emp.startYear}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

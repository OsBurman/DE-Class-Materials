import { useState, useMemo, useCallback } from 'react';

/**
 * TODO Task 2: Implement useDataFilter(data, filters)
 *
 * filteredData — useMemo that recalculates only when data or filters change
 *   Filter rules:
 *   - If filters.dept is non-empty, keep items where item.dept includes filters.dept (case-insensitive)
 *   - If filters.minSalary is non-empty, keep items where item.salary >= Number(filters.minSalary)
 *
 * handleFilterChange — useCallback so the function reference is stable across renders
 *   Signature: (key, value) => setFilters(prev => ({ ...prev, [key]: value }))
 *   Dependency array: [] — should never change
 *
 * Return { filteredData, handleFilterChange, filters }
 */
export function useDataFilter(data, externalFilters) {
  // Note: if externalFilters is passed in, use it directly.
  // If managing filters internally, use useState here.

  const filteredData = useMemo(() => {
    // TODO Task 2: Apply filter logic
    return data;
  }, [data, externalFilters]);

  const handleFilterChange = useCallback((key, value) => {
    // TODO Task 2: Update filters
    console.log('Filter changed:', key, value);
  }, []);

  return { filteredData, handleFilterChange };
}

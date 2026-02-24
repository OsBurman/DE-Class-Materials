// src/hocs/withAdminOnly.jsx  (solution)
import React from 'react';

function withAdminOnly(WrappedComponent) {
  function Enhanced({ isAdmin, ...rest }) {
    // Gate access — non-admins see an error message instead of the component
    if (!isAdmin) {
      return <p>Access Denied. Admins only.</p>;
    }
    return <WrappedComponent {...rest} />;
  }

  Enhanced.displayName = `withAdminOnly(${WrappedComponent.displayName || WrappedComponent.name})`;
  return Enhanced;
}

export default withAdminOnly;

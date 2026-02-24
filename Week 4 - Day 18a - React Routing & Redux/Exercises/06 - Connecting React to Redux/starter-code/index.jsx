// src/index.jsx  (starter)
import React from 'react';
import ReactDOM from 'react-dom/client';
// TODO 1: Import Provider from 'react-redux'.
// TODO 2: Import store from './store/store'.
import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root'));

// TODO 3: Wrap <App /> in <Provider store={store}> so every component
//         in the tree can access the Redux store.
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

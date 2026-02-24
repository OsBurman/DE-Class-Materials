import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App.jsx';
import './App.css';
// TODO Task 3: Import Provider from 'react-redux' and store from './store/store'
// Wrap <App /> with <Provider store={store}> inside <BrowserRouter>

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    {/* TODO Task 3: Add <Provider store={store}> wrapper here */}
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>
);

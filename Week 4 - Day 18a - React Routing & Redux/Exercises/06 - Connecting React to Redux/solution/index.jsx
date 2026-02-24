// src/index.jsx  (solution)
import React from 'react';
import ReactDOM from 'react-dom/client';
import { Provider } from 'react-redux'; // Provider injects the store into React's context
import store from './store/store';
import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root'));

// Every component below <Provider> can now call useSelector/useDispatch
root.render(
  <React.StrictMode>
    <Provider store={store}>
      <App />
    </Provider>
  </React.StrictMode>
);

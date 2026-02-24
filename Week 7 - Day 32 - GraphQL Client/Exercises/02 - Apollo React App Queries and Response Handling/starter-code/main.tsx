import React from "react";
import ReactDOM from "react-dom/client";
import { ApolloProvider } from "@apollo/client";
import { App } from "./App";
import { client } from "./client";

// TODO 2: Wrap <App /> with <ApolloProvider client={client}> so that every
//         component in the tree can use useQuery / useMutation.
//
// Replace the ReactDOM.createRoot call below with the correct JSX.

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    {/* TODO 2: Add ApolloProvider wrapper here */}
    <App />
  </React.StrictMode>
);

import React from "react";
import ReactDOM from "react-dom/client";
import { ApolloProvider } from "@apollo/client";
import { App } from "./App";
import { client } from "./client";

// ApolloProvider makes the client available to all child components via React context.
// Every useQuery / useMutation call in the tree will use this client instance.
ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <ApolloProvider client={client}>
      <App />
    </ApolloProvider>
  </React.StrictMode>
);

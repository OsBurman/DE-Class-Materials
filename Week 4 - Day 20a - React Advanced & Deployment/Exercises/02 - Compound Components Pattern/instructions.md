# Exercise 02 — Compound Components Pattern

## Objective
Practice the compound components pattern to build a flexible, composable UI widget that shares implicit state across child components via React Context.

## Background
The compound components pattern lets you build components like `<Select>` or `<Tabs>` where a parent manages shared state and its child components automatically have access to it — no prop drilling required. Libraries like Radix UI and Headless UI are built entirely around this pattern.

## Requirements
1. Build a `<Tabs>` compound component family with the following API:
   ```tsx
   <Tabs defaultTab="a">
     <Tabs.List>
       <Tabs.Tab id="a">Tab A</Tabs.Tab>
       <Tabs.Tab id="b">Tab B</Tabs.Tab>
       <Tabs.Tab id="c">Tab C</Tabs.Tab>
     </Tabs.List>
     <Tabs.Panel id="a">Content for Tab A</Tabs.Panel>
     <Tabs.Panel id="b">Content for Tab B</Tabs.Panel>
     <Tabs.Panel id="c">Content for Tab C</Tabs.Panel>
   </Tabs>
   ```
2. `Tabs` must:
   - Accept a `defaultTab` prop (the initially active tab id).
   - Create a React Context that provides `{ activeTab, setActiveTab }`.
   - Render its `children` wrapped in the Context provider.
3. `Tabs.List` must render a `<div>` wrapping its children (the tab buttons).
4. `Tabs.Tab` must:
   - Accept an `id` prop.
   - Read `activeTab` and `setActiveTab` from context.
   - Render a `<button>` that calls `setActiveTab(id)` on click.
   - Apply `fontWeight: 'bold'` and `borderBottom: '2px solid blue'` when `activeTab === id`.
5. `Tabs.Panel` must:
   - Accept an `id` prop.
   - Read `activeTab` from context.
   - Only render its children when `activeTab === id`.
6. Attach `Tabs.List`, `Tabs.Tab`, and `Tabs.Panel` as static properties on `Tabs`.
7. Render the component in `App` with at least 3 tabs and meaningful panel content.

## Hints
- Create the Context outside the `Tabs` component with a sensible default value.
- Use `useContext` inside `Tabs.Tab` and `Tabs.Panel` to read the shared state.
- Attaching sub-components as static properties (`Tabs.List = ...`) must be done after all component definitions.
- You will need a custom `TabsContext` type to give `useContext` proper types.

## Expected Output
```
[Tab A] [Tab B] [Tab C]       ← Tab A is bold/underlined by default

Content for Tab A             ← only this panel is visible initially

After clicking Tab B:
[Tab A] [Tab B] [Tab C]       ← Tab B is now bold/underlined
Content for Tab B
```

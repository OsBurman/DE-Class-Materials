// store/notificationsSlice.js  (starter)
import { createSlice } from '@reduxjs/toolkit';

// TODO 2: Define initialState with shape: { messages: [], unreadCount: 0 }
const initialState = {
  // TODO: add messages array and unreadCount
};

// TODO 3: Create a notificationsSlice with name 'notifications' and three reducers:
//
//   addNotification(state, action)
//     - action.payload is a string (the message text)
//     - Push { id: Date.now(), text: action.payload, read: false } to state.messages
//     - Increment state.unreadCount
//
//   markAllRead(state)
//     - Set every message's read property to true
//     - Reset state.unreadCount to 0
//
//   clearNotifications(state)
//     - Reset state.messages to []
//     - Reset state.unreadCount to 0

const notificationsSlice = createSlice({
  name: 'notifications',
  initialState,
  reducers: {
    addNotification(state, action) {
      // TODO: implement
    },
    markAllRead(state) {
      // TODO: implement
    },
    clearNotifications(state) {
      // TODO: implement
    },
  },
});

// TODO 4: Export the action creators
export const { addNotification, markAllRead, clearNotifications } = notificationsSlice.actions;

// TODO 5: Export the reducer as default
export default notificationsSlice.reducer;

// store/notificationsSlice.js  (solution)
import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  messages: [],
  unreadCount: 0,
};

const notificationsSlice = createSlice({
  name: 'notifications',
  initialState,
  reducers: {
    addNotification(state, action) {
      // action.payload is the message string
      state.messages.push({ id: Date.now(), text: action.payload, read: false });
      state.unreadCount += 1;
    },

    markAllRead(state) {
      // Immer lets us mutate array items in place
      state.messages.forEach(msg => { msg.read = true; });
      state.unreadCount = 0;
    },

    clearNotifications(state) {
      state.messages = [];
      state.unreadCount = 0;
    },
  },
});

export const { addNotification, markAllRead, clearNotifications } = notificationsSlice.actions;
export default notificationsSlice.reducer;

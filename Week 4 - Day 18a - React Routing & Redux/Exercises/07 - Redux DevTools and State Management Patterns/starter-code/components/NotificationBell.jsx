// src/components/NotificationBell.jsx  (starter)
import React from 'react';
// TODO 8: Import useSelector and useDispatch from 'react-redux'.
// TODO 9: Import addNotification and markAllRead from '../store/notificationsSlice'.

let msgCounter = 1; // simple counter for unique demo messages

function NotificationBell() {
  // TODO 10: Use useSelector to read state.notifications.unreadCount.
  const unreadCount = 0; // replace with useSelector

  // TODO 11: Call useDispatch().
  const dispatch = null; // replace with useDispatch()

  function handleAdd() {
    // TODO 12: Dispatch addNotification with a string message like `'Notification #${msgCounter++}'`.
  }

  function handleMarkRead() {
    // TODO 13: Dispatch markAllRead().
  }

  return (
    <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
      <span>🔔 {unreadCount} unread</span>
      <button onClick={handleAdd}>Add Notification</button>
      <button onClick={handleMarkRead}>Mark All Read</button>
    </div>
  );
}

export default NotificationBell;

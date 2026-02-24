// src/components/NotificationBell.jsx  (solution)
import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { addNotification, markAllRead } from '../store/notificationsSlice';

let msgCounter = 1;

function NotificationBell() {
  // Subscribe only to the unread count — avoids re-renders on unrelated state changes
  const unreadCount = useSelector(state => state.notifications.unreadCount);
  const dispatch = useDispatch();

  function handleAdd() {
    dispatch(addNotification(`Notification #${msgCounter++}`));
  }

  function handleMarkRead() {
    dispatch(markAllRead());
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

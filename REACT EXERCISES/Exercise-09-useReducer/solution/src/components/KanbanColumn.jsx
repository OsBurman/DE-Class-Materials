import TaskCard from './TaskCard'

function KanbanColumn({ label, tasks, dispatch }) {
  return (
    <div className="kanban-column">
      <div className="column-header">
        <h2>{label}</h2>
        <span className="task-count">{tasks.length}</span>
      </div>
      <div className="column-body">
        {tasks.length === 0
          ? <p className="empty-col">No tasks here</p>
          : tasks.map(task => <TaskCard key={task.id} task={task} dispatch={dispatch} />)
        }
      </div>
    </div>
  )
}

export default KanbanColumn

// src/components/TeamMemberCard.jsx
// This component renders a single developer's card.
// Follow the TODOs in order — each one builds on the last.

// TODO 1: Destructure the following props from the function parameter:
//         name, role, skills, isAvailable, avatarUrl
//
//         Syntax reminder:
//         function TeamMemberCard({ prop1, prop2 }) { ... }
function TeamMemberCard() {
  return (
    <div className="card">

      {/* TODO 2: Render the developer's avatar.
          Use an <img> tag.
          - Set src={avatarUrl}
          - Set alt={name}
          - Give it className="card-avatar" */}


      {/* TODO 3: Render the developer's name inside an <h2> tag */}


      {/* TODO 4: Render the developer's role inside a <p> tag */}


      {/* TODO 5: Conditionally render an availability badge using a TERNARY operator.
          - If isAvailable is true  → <span className="badge badge--available">✅ Available</span>
          - If isAvailable is false → <span className="badge badge--unavailable">🔒 On Project</span>
          
          Syntax reminder:
          { condition ? <ElementIfTrue /> : <ElementIfFalse /> } */}


      {/* TODO 6: Render the skills list.
          - Add a <div className="skills"> wrapper
          - Inside it, use skills.map() to render each skill as:
            <span key={skill} className="skill-tag">{skill}</span>
          
          Syntax reminder:
          { array.map(item => <Tag key={item}>{item}</Tag>) } */}

    </div>
  );
}

export default TeamMemberCard;

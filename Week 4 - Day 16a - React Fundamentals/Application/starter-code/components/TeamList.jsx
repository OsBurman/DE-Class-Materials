// src/components/TeamList.jsx
// This component receives the full array of team members and renders a card for each one.

// TODO 1: Import the TeamMemberCard component from './TeamMemberCard'


// TODO 2: Destructure the `members` prop from the function parameter
function TeamList() {

  // TODO 3: Add an empty-state check using the && short-circuit operator.
  //         If members.length === 0, render:
  //         <p>No team members found.</p>
  //
  //         Syntax reminder:
  //         { condition && <Element /> }


  return (
    <main>
      <div className="team-grid">

        {/* TODO 4: Use members.map() to render a <TeamMemberCard /> for each member.
            - Use member.id as the key prop
            - Pass all required props: name, role, skills, isAvailable, avatarUrl
            
            Syntax reminder:
            { members.map(member => (
                <TeamMemberCard
                  key={member.id}
                  name={member.name}
                  ...
                />
            )) } */}

      </div>
    </main>
  );
}

export default TeamList;

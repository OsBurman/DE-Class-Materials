// src/data/teamMembers.js
// This is the data array your App component will import and pass down to TeamList.
// Each object represents one developer on the team.

const teamMembers = [
  {
    id: 1,
    name: "Alex Rivera",
    role: "Frontend Developer",
    skills: ["React", "JavaScript", "CSS", "HTML"],
    isAvailable: true,
    avatarUrl: "https://i.pravatar.cc/150?img=11",
  },
  {
    id: 2,
    name: "Jordan Kim",
    role: "Backend Developer",
    skills: ["Java", "Spring Boot", "SQL", "REST APIs"],
    isAvailable: false,
    avatarUrl: "https://i.pravatar.cc/150?img=32",
  },
  {
    id: 3,
    name: "Morgan Patel",
    role: "Full Stack Developer",
    skills: ["React", "Node.js", "MongoDB", "TypeScript"],
    isAvailable: true,
    avatarUrl: "https://i.pravatar.cc/150?img=47",
  },
  {
    id: 4,
    name: "Taylor Brooks",
    role: "DevOps Engineer",
    skills: ["Docker", "Kubernetes", "AWS", "CI/CD"],
    isAvailable: false,
    avatarUrl: "https://i.pravatar.cc/150?img=25",
  },
  {
    id: 5,
    name: "Casey Nguyen",
    role: "UI/UX Designer",
    skills: ["Figma", "CSS", "Accessibility", "Prototyping"],
    isAvailable: true,
    avatarUrl: "https://i.pravatar.cc/150?img=56",
  },
];

export default teamMembers;

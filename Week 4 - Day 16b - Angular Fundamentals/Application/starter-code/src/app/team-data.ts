// team-data.ts — provided, do not modify

export interface TeamMember {
  id: number;
  name: string;
  role: string;
  skills: string[];
  isAvailable: boolean;
  avatarUrl: string;
}

export const teamMembers: TeamMember[] = [
  {
    id: 1,
    name: 'Alex Johnson',
    role: 'Frontend Engineer',
    skills: ['React', 'TypeScript', 'CSS'],
    isAvailable: true,
    avatarUrl: 'https://i.pravatar.cc/150?img=1'
  },
  {
    id: 2,
    name: 'Maria Garcia',
    role: 'Backend Engineer',
    skills: ['Java', 'Spring Boot', 'PostgreSQL'],
    isAvailable: false,
    avatarUrl: 'https://i.pravatar.cc/150?img=5'
  },
  {
    id: 3,
    name: 'James Lee',
    role: 'Full Stack Engineer',
    skills: ['Angular', 'Node.js', 'MongoDB'],
    isAvailable: true,
    avatarUrl: 'https://i.pravatar.cc/150?img=3'
  },
  {
    id: 4,
    name: 'Priya Patel',
    role: 'DevOps Engineer',
    skills: ['Docker', 'Kubernetes', 'AWS'],
    isAvailable: true,
    avatarUrl: 'https://i.pravatar.cc/150?img=9'
  },
  {
    id: 5,
    name: 'Sam Chen',
    role: 'Data Engineer',
    skills: ['Python', 'Kafka', 'Spark'],
    isAvailable: false,
    avatarUrl: 'https://i.pravatar.cc/150?img=7'
  }
];

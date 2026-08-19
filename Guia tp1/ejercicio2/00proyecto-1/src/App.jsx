import './App.css'
import { TwitterFollowCard } from './TwitterFollowCard'

const users = [
  {
    userName: 'JPabloPenar',
    name: 'Juan Pablo Peñaranda',
    isFollowing: true
  },
  {
    userName: 'GabrielLeiva05',
    name: 'Gabriel Leiva',
    isFollowing: false
  },
  {
    userName: 'JoacoVillegas',
    name: 'Joaquin Villegas',
    isFollowing: true
  },
  {
    userName: 'franualba',
    name: 'Francisco Alba',
    isFollowing: false
  }
]



function App() {
  return (
    <section className='App'>
      {
        users.map(({ userName, name, isFollowing }) => (
          <TwitterFollowCard
            key={userName}
            userName={userName}
            initialIsFollowing={isFollowing}
          >
            {name}
          </TwitterFollowCard>
        ))
      }
    </section>
  )
}

export default App

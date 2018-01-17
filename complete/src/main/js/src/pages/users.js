import * as React from 'react'
import {withFetch} from '../components/with-fetch'
import {compose} from 'recompose'
import {Header} from '../components/header'
import {Container} from '../components/container'
import {Text} from '../components/text'
import {Link} from 'react-router-dom'
import {AnimatedText} from '../components/pagination-list'
const enhance = compose(
  withFetch({
    url: 'http://localhost:8080/users',
  }),
)
const StatlessUsers = ({data}) => {
  return (
    <Container
      backgroundColor={'rgb(250,250,250)'}
      height={'100vh'}
      width={'100vw'}
    >
      <Container
        style={{flexDirection: 'column'}}
        justify="flex-start"
        alignItems="initial"
        height={'80vh'}
        width="80vw"
        backgroundColor={'rgb(250,250,250)'}
      >
        <Header>Users</Header>
        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
          }}
        >
          {data.map((user, i) => {
            return (
              <Container
                key={i}
                backgroundColor={'rgb(250,250,250)'}
                justify="space-around"
                style={{
                  flexDirection: 'row',
                }}
              >
                <AnimatedText>{user}</AnimatedText>
                <Link style={{textDecoration: 'none'}} to={`user/${user}`}>
                  <AnimatedText style={{color: 'rgb(120, 182, 202)'}}>View Recomendations</AnimatedText>
                </Link>
              </Container>
            )
          })}
        </div>
      </Container>
    </Container>
  )
}

export const Users = enhance(StatlessUsers)

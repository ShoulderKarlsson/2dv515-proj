import * as React from 'react'
import {withFetch} from '../components/with-fetch'
import {compose} from 'recompose'
import {Header} from '../components/header'
import {Container} from '../components/container'
import {Link} from 'react-router-dom'
import {AnimatedText} from '../components/animated-text'
import {NoScrollbarsDiv} from '../components/pagination-list'

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
        height={'80vh'}
        backgroundColor={'rgb(250,250,250)'}
        width={'80vw'}
        justify={'flex-start'}
        alignItems="initial"
        style={{flexDirection: 'column'}}
      >
        <div style={{flex: 1}}>
          <Header>Users</Header>
        </div>
        <NoScrollbarsDiv style={{flex: 10, overflowY: 'scroll'}}>
          {data.map((user, i) => {
            return (
              <div style={{display: 'flex', justifyContent: 'space-around'}} key={i}>
                <AnimatedText>{user}</AnimatedText>
                <Link style={{textDecoration: 'none'}} to={`user/${user}`}>
                  <AnimatedText style={{color: 'rgb(120, 182, 202)'}}>
                    View Recomendations
                  </AnimatedText>
                </Link>
              </div>
            )
          })}
        </NoScrollbarsDiv>
      </Container>
    </Container>
  )
}

export const Users = enhance(StatlessUsers)

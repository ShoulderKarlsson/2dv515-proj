import * as React from 'react'
import {compose, mapProps} from 'recompose'
import {withFetch} from '../components/with-fetch'
import {PaginationList} from '../components/pagination-list'
import {Container} from '../components/container'

const enhance = compose(
  withFetch({
    urlGenerator: props =>
      `http://localhost:8080/rec/${props.match.params.username}`,
  }),
  mapProps(({data: {userRecs, movieRecs}, ...props}) => {
    const transform = object =>
      Object.keys(object).reduce(
        (acc, curr) => [...acc, {id: curr, value: object[curr].toFixed(3)}],
        [],
      )

    return {
      ...props,
      movieRecs: transform(movieRecs).slice(0, 15),
      userRecs: transform(userRecs).slice(0, 15),
    }
  }),
)

const StatelessUserRecommendation = ({movieRecs, userRecs, ...props}) => {
  return (
    <Container
      backgroundColor={'rgb(250,250,250)'}
      height={'100vh'}
      width={'100vw'}
    >
      <Container
        // backgroundColor={'rgb(250,250,250)'}
        backgroundColor={'orange'}
        style={{flexDirection: 'row'}}
        justify={'space-around'}
        height={'80vh'}
        width={'80vw'}
      >
        <PaginationList
          text={'Movie Recommendations'}
          end={2}
          data={movieRecs}
        />
        <PaginationList text={'User Recommendations'} end={2} data={userRecs} />
      </Container>
    </Container>
  )
}

export const UserRecommendation = enhance(StatelessUserRecommendation)

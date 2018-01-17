import * as React from 'react'
import {compose, lifecycle, withState} from 'recompose'
import {get} from '../lib/http'
import Spinner from 'react-spinkit'
import {Fade} from '../components/fade'

export const withFetch = ({
  url = '',
  urlGenerator,
}) => WrappedComponent => props => {
  const enhance = compose(
    withState('isLoading', 'setIsLoading', true),
    withState('data', 'setData', 1),
    withState('error', 'setError', null),
    lifecycle({
      async componentDidMount() {
        if (typeof urlGenerator === 'function' && url === '') {
          const url = urlGenerator(props)
          const response = await get(url).catch(e => console.log)
          this.props.setData(response)
          return this.props.setIsLoading(false)
        }

        const response = await get(url).catch(e => this.props.setError(e))

        setTimeout(() => {
          this.props.setData(response)
          this.props.setIsLoading(false)
        }, 3000)
      },
    }),
  )

  const WithFetch = enhance(({isLoading, data, ...withFetchProps}) => {
    return isLoading ? (
      <Fade
        style={{
          width: '100vw',
          height: '100vh',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
        }}
      >
        <Spinner fadeIn={'none'} name="pacman" />
      </Fade>
    ) : (
      <Fade>
        <WrappedComponent data={data} {...withFetchProps} />
      </Fade>
    )
  })

  return <WithFetch />
}

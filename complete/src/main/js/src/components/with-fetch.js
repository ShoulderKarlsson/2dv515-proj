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
        if (!url && typeof urlGenerator !== 'function') {
          throw new Error('Must provide a URL or a URL generator function')
        }

        const _url =
          typeof urlGenerator === 'function' ? urlGenerator(props) : url

        const response = await get(_url).catch(e => {
          console.log(e)
          this.props.setError(e)
        })

        this.props.setData(response)
        this.props.setIsLoading(false)
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
        <WrappedComponent data={data} {...withFetchProps} {...props} />
      </Fade>
    )
  })

  return <WithFetch />
}

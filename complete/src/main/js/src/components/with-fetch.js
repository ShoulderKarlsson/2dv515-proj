import * as React from 'react'
import {compose, lifecycle, withState} from 'recompose'
import {get} from '../lib/http'

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
          // console.log('Url generated with generator: ', url)
          // const response = await get(url).catch(e => console.log)
          // console.log(response)
          this.props.setData({
            userRecs: {
              User2: 0.10810810810810811,
              User1: 0.2222222222222222,
              User5: 0.11764705882352941,
              User4: 0.3076923076923077,
              User3: 0.23529411764705882,
            },
            movieRecs: {
              Super: 3.8051914216761737,
              Night: 3.3561594997806057,
              Snake: 3.7146089293549807,
              Luck: 2.461988486074374,
              Dupree: 2.5725647213690217,
              Lady: 2.852963389196576,
            },
          })
          return this.props.setIsLoading(false)
        }

        // const response = await get(url)
        //   .catch(e => this.props.setError(e))

        setTimeout(() => {
          const response = [
            'User2',
            'User1',
            'User6',
            'User5',
            'User4',
            'User3',
          ]
          this.props.setData(response)
          this.props.setIsLoading(false)
        }, 1000)
      },
    }),
  )

  const WithFetch = enhance(({isLoading, data, ...withFetchProps}) => {
    console.log('isLoading :', isLoading)
    return isLoading ? (
      <div
        style={{
          width: '100%',
          height: '100%',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
        }}
      >
        <p>Spinner..</p>
      </div>
    ) : (
      <WrappedComponent data={data} {...withFetchProps} />
    )
  })

  return <WithFetch />
}

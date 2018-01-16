import * as React from 'react'
import {compose, withState} from 'recompose'
import {Container} from './container'
import {Header} from '../components/header'
import {Text} from '../components/text'
const enhance = compose(withState('end', 'setEnd', ({end}) => (end ? end : 0)))

const StatelessPaginationList = ({end, setEnd, data, text = '', ...props}) => {
  return (
    <div
      style={{
        background: 'green',
        height: '100%',
        width: '100%',
        display: 'flex',
        alignItems: 'center',
        flexDirection: 'column'
      }}
    >
      <Header>{text}</Header>
      <div style={{overflowY: 'scroll', minHeight: '70%', width: '100%', textAlign: 'center'}} className='list-container'>
        {data.slice(0, end).map(({id, value}) => <Text>{id} - {value}</Text>)}
      </div>
      <input value={'Load 5 more'} type='button' onClick={() => {
        setEnd(end + 5)
      }}/>
    </div>
  )
}

export const PaginationList = enhance(StatelessPaginationList)

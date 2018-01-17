import * as React from 'react'
import {compose, withState} from 'recompose'
import {Container} from './container'
import {Header} from '../components/header'
import {Text} from '../components/text'
import styled from 'styled-components'
import {AnimatedText} from '../components/animated-text'

const NoScrollbarsDiv = styled.div`
  ::-webkit-scrollbar {
    display: none;
  }
`
const StyledButton = styled.input`
  border: none;
  padding: 16px;
  width: 30%;
  background: rgb(120, 182, 202);
  color: white;
`

const enhance = compose(withState('end', 'setEnd', ({end}) => (end ? end : 0)))
const StatelessPaginationList = ({end, setEnd, data, text = '', ...props}) => {
  return (
    <div
      style={{
        height: '100%',
        width: '100%',
        display: 'flex',
        alignItems: 'center',
        flexDirection: 'column',
      }}
    >
      <Header>{text}</Header>
      <NoScrollbarsDiv
        style={{
          overflowY: 'scroll',
          minHeight: '70%',
          width: '100%',
          textAlign: 'center',
        }}
      >
        {data.slice(0, end).map(({id, value}) => (
          <AnimatedText>
            {id} - {value}
          </AnimatedText>
        ))}
      </NoScrollbarsDiv>
      <StyledButton
        value={'Load 5 more'}
        type="button"
        onClick={() => {
          setEnd(end + 5)
        }}
      />
    </div>
  )
}

export const PaginationList = enhance(StatelessPaginationList)

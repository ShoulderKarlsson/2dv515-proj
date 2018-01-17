import {Text} from './text'
import {keyframes} from 'styled-components'
import styled from 'styled-components'

const fadeIn = keyframes`
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
`

export const AnimatedText = styled(Text)`
  animation: ${fadeIn};
  animation-duration: 500ms;
  animation-timing-function: ease-out;
`

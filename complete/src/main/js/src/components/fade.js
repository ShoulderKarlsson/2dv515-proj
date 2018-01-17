import styled, {keyframes} from 'styled-components'

const fadeIn = keyframes`
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
`

export const Fade = styled.div`
  animation: ${fadeIn};
  animation-duration: 500ms;
  animation-timing-function: ease-out;
`

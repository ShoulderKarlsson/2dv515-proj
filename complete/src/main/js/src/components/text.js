import styled from 'styled-components'

export const Text = styled.p`
  font-family: 'Roboto';
  font-size: ${props => props.size ? props.size : 'initial'};
`

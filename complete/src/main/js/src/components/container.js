import styled from 'styled-components'

export const Container = styled.div`
  display: flex;
  justify-content: ${props => (props.justify ? props.justify : 'center')};
  align-items: ${props => (props.alignItems ? props.alignItems : 'center')};
  height: ${props => (props.height ? props.height : 'auto')};
  width: ${props => (props.width ? props.width : 'auto')};
  background-color: ${props =>
    props.backgroundColor ? props.backgroundColor : 'white'};
  margin-top: ${props => (props.marginTop ? props.marginTop : 'initial')};
  margin-bottom: ${props => (props.marginBottom ? props.marginTop : 'initial')};
  overflow: ${props => props.overflow ? props.overflow : 'hidden'};
`

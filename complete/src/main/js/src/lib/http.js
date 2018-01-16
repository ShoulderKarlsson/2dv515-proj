import fetch from 'isomorphic-fetch'

const setMethod = method => async url => {
  const response = await fetch(url, {
    method,
  })

  if (typeof response.json === 'function') {
    return response.json()
  }

  return response
}

export const get = setMethod('get')

import { VueQueryPlugin, type VueQueryPluginOptions } from '@tanstack/vue-query'

export const queryPluginOptions: VueQueryPluginOptions = {
  queryClientConfig: {
    defaultOptions: {
      queries: {
        staleTime: 1000 * 60 * 2,
        retry: 1,
        refetchOnWindowFocus: false,
      },
    },
  },
}

export { VueQueryPlugin }

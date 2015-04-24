library(dplyr)
library(ggplot2)
df <- read.table('output.txt', skip=1, col.names=c('length', 'benchmark', 'impl', 'ns', 'v5')) %>%
  tbl_df %>% select(-v5)

df %>% filter(length == 10000) %>% 
  ggplot(aes(x=benchmark, y=log(ns), fill=impl)) + geom_bar(stat='identity', position='dodge') + xlab('') + ggtitle('10k records') +
  theme(axis.text.x=element_text(angle=45))
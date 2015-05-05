library(dplyr)
library(ggplot2)

df <- read.table('output2.txt', skip=1, col.names=c('length', 'benchmark', 'impl', 'order', 'ns', 'v5')) %>%
  tbl_df %>% select(-v5)

plot <- df %>% 
  ggplot(aes(x=benchmark, y=log(ns), fill=impl)) + geom_bar(stat='identity', position='dodge') + xlab('') +
  facet_grid(length ~ order)
  
ggsave('plot.png', plot, width=10, height=5, units="in", dpi=100)
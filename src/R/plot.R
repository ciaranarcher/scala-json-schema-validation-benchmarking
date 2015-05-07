library(dplyr)
library(ggplot2)

df <- read.table('output.txt', skip=1, col.names=c('length', 'impl', 'benchmark', 'order', 'ns', 'v5')) %>%
  tbl_df %>% select(-v5)

`%like%` <- function(v, pattern) grepl(pattern, v, perl=TRUE)

grouped <- df %>% filter(!(impl %in% c('SC_M_LsM', 'JU_C_HM', 'JU_TM'))) %>% 
  filter(!(impl %like% "^SC_I.+")) %>%
  group_by(benchmark, length, order) %>% mutate(average=mean(ns)) %>% group_by

plot <- grouped %>% 
  ggplot(aes(x=impl, y=log(ns/average), fill=impl)) + 
  geom_bar(stat='identity', position='dodge') + xlab('') +
  facet_grid(length ~ order + benchmark) + 
  theme(axis.text.x=element_blank(), axis.ticks.x=element_blank())

plot

ggsave('plot.png', plot, width=10, height=5, units="in", dpi=100)
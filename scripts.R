
library(ggplot2)

matrix <- read.csv("fileComparisons.csv")
png("commons-compress-duplicates.png",width=3000,height=2500)
ggplot(matrix,aes(From,To)) + geom_tile(aes(fill=Overlap)) + theme(axis.text.x=element_text(angle=90, size=8),axis.text.y=element_text(size=8))
dev.off()

detailed <- read.csv("detailedComparisons.csv",header=T)
png("detailed.png",width=3000,height=2500)
ggplot(detailed,aes(FromLine,ToLine)) + geom_tile(aes(fill=Match)) + theme(axis.text.x=element_text(angle=90, size=8),axis.text.y=element_text(size=8))
dev.off()

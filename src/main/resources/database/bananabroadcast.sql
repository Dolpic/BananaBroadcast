CREATE TABLE IF NOT EXISTS `cartouches` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `title` text NOT NULL,
  `panels` text NOT NULL,
  `duration` int(11) NOT NULL COMMENT 'in seconds',
  `addTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `startTime` int(11) NOT NULL,
  `endTime` int(11) NOT NULL,
  `path` text NOT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE IF NOT EXISTS `cartouches_categories` (
  `ID` int(11) NOT NULL,
  `categorie` varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS `jingles` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `title` text NOT NULL,
  `isLiner` tinyint(1) NOT NULL,
  `duration` int(11) NOT NULL COMMENT 'in seconds',
  `addTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `startTime` int(11) NOT NULL,
  `endTime` int(11) NOT NULL,
  `path` text NOT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE IF NOT EXISTS `jingles_categories` (
  `ID` int(11) NOT NULL,
  `categorie` varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS `musics` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `title` text NOT NULL,
  `artist` text NOT NULL,
  `album` text NOT NULL,
  `albumIndex` int(11) NOT NULL COMMENT 'song number in the album',
  `genre` text NOT NULL,
  `duration` int(11) NOT NULL COMMENT 'in seconds',
  `addTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `startTime` int(11) NOT NULL,
  `endTime` int(11) NOT NULL,
  `path` text NOT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE IF NOT EXISTS `musics_categories` (
  `ID` int(11) NOT NULL,
  `categorie` varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS `scheduler` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `musicId` int(11) NOT NULL,
  `playOrder` int(11) NOT NULL,
  `slot` tinyint(4) NOT NULL,
  `day` date NOT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE IF NOT EXISTS `scheduler_default` (
  `day` int(11) NOT NULL,
  `hour` int(11) NOT NULL,
  `categories` text NOT NULL,
  PRIMARY KEY (`day`,`hour`)
);

CREATE TABLE IF NOT EXISTS `scheduler_planning` (
  `date` datetime NOT NULL,
  `categories` text NOT NULL,
  PRIMARY KEY (`date`)
);

CREATE TABLE IF NOT EXISTS `tapis` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `title` text NOT NULL,
  `isLiner` tinyint(1) NOT NULL,
  `duration` int(11) NOT NULL COMMENT 'in seconds',
  `addTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `startTime` int(11) NOT NULL,
  `endTime` int(11) NOT NULL,
  `path` text NOT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE IF NOT EXISTS `tapis_categories` (
  `ID` int(11) NOT NULL,
  `categorie` varchar(255) NOT NULL
);
-- phpMyAdmin SQL Dump
-- version 4.0.4.2
-- http://www.phpmyadmin.net
--
-- Client: localhost
-- Généré le: Ven 05 Juin 2020 à 17:10
-- Version du serveur: 5.6.13
-- Version de PHP: 5.4.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données: `bananabroadcast`
--
CREATE DATABASE IF NOT EXISTS `bananabroadcast` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `bananabroadcast`;

-- --------------------------------------------------------

--
-- Structure de la table `cartouches`
--

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=7 ;

-- --------------------------------------------------------

--
-- Structure de la table `cartouches_categories`
--

CREATE TABLE IF NOT EXISTS `cartouches_categories` (
  `ID` int(11) NOT NULL,
  `categorie` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `jingles`
--

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=7 ;

-- --------------------------------------------------------

--
-- Structure de la table `jingles_categories`
--

CREATE TABLE IF NOT EXISTS `jingles_categories` (
  `ID` int(11) NOT NULL,
  `categorie` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `musics`
--

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=32 ;

-- --------------------------------------------------------

--
-- Structure de la table `musics_categories`
--

CREATE TABLE IF NOT EXISTS `musics_categories` (
  `ID` int(11) NOT NULL,
  `categorie` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `scheduler`
--

CREATE TABLE IF NOT EXISTS `scheduler` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `musicId` int(11) NOT NULL,
  `playOrder` int(11) NOT NULL,
  `slot` tinyint(4) NOT NULL,
  `day` date NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=110 ;

-- --------------------------------------------------------

--
-- Structure de la table `scheduler_default`
--

CREATE TABLE IF NOT EXISTS `scheduler_default` (
  `day` int(11) NOT NULL,
  `hour` int(11) NOT NULL,
  `categories` text NOT NULL,
  PRIMARY KEY (`day`,`hour`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `scheduler_planning`
--

CREATE TABLE IF NOT EXISTS `scheduler_planning` (
  `date` datetime NOT NULL,
  `categories` text NOT NULL,
  PRIMARY KEY (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `tapis`
--

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=7 ;

-- --------------------------------------------------------

--
-- Structure de la table `tapis_categories`
--

CREATE TABLE IF NOT EXISTS `tapis_categories` (
  `ID` int(11) NOT NULL,
  `categorie` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

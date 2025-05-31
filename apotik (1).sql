-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 31, 2025 at 04:12 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `apotik`
--

-- --------------------------------------------------------

--
-- Table structure for table `detail_transaksi_penjualan`
--

CREATE TABLE `detail_transaksi_penjualan` (
  `detail_id` int(11) NOT NULL,
  `transaksi_id` int(11) DEFAULT NULL,
  `obat_id` int(11) DEFAULT NULL,
  `jumlah` int(11) NOT NULL,
  `harga` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `detail_transaksi_penjualan`
--

INSERT INTO `detail_transaksi_penjualan` (`detail_id`, `transaksi_id`, `obat_id`, `jumlah`, `harga`) VALUES
(1, 1, 3001, 30, 10000.00),
(2, 1, 3002, 45, 5000.00);

-- --------------------------------------------------------

--
-- Table structure for table `kategori_obat`
--

CREATE TABLE `kategori_obat` (
  `kategori_id` int(11) NOT NULL,
  `nama_kategori` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `kategori_obat`
--

INSERT INTO `kategori_obat` (`kategori_id`, `nama_kategori`) VALUES
(301, 'obat pusing'),
(302, 'obat Maag'),
(303, 'Obat Panas');

-- --------------------------------------------------------

--
-- Table structure for table `obat`
--

CREATE TABLE `obat` (
  `obat_id` int(11) NOT NULL,
  `nama_obat` varchar(255) NOT NULL,
  `harga` decimal(10,2) NOT NULL,
  `stok` int(11) NOT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `kategori_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `obat`
--

INSERT INTO `obat` (`obat_id`, `nama_obat`, `harga`, `stok`, `supplier_id`, `kategori_id`) VALUES
(3001, 'Panadol', 15000.00, 27, 201, 301),
(3002, 'Promag', 2000.00, 120, 202, 302);

-- --------------------------------------------------------

--
-- Table structure for table `supplier`
--

CREATE TABLE `supplier` (
  `supplier_id` int(11) NOT NULL,
  `nama_supplier` varchar(100) NOT NULL,
  `alamat` varchar(255) DEFAULT NULL,
  `kontak` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `supplier`
--

INSERT INTO `supplier` (`supplier_id`, `nama_supplier`, `alamat`, `kontak`) VALUES
(201, 'kimia_farma', 'jalan mawar', '085731'),
(202, 'sido_muncul', 'jalaan melati', '085521'),
(203, 'kalbe_farma', 'jalan kalbe', '082321');

-- --------------------------------------------------------

--
-- Table structure for table `transaksi_penjualan`
--

CREATE TABLE `transaksi_penjualan` (
  `transaksi_id` int(11) NOT NULL,
  `tanggal` date NOT NULL,
  `total_harga` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaksi_penjualan`
--

INSERT INTO `transaksi_penjualan` (`transaksi_id`, `tanggal`, `total_harga`) VALUES
(1, '2023-02-12', 37000.00),
(2, '2023-03-07', 54000.00);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `detail_transaksi_penjualan`
--
ALTER TABLE `detail_transaksi_penjualan`
  ADD PRIMARY KEY (`detail_id`),
  ADD KEY `transaksi_id` (`transaksi_id`),
  ADD KEY `obat_id` (`obat_id`);

--
-- Indexes for table `kategori_obat`
--
ALTER TABLE `kategori_obat`
  ADD PRIMARY KEY (`kategori_id`);

--
-- Indexes for table `obat`
--
ALTER TABLE `obat`
  ADD PRIMARY KEY (`obat_id`),
  ADD KEY `supplier_id` (`supplier_id`),
  ADD KEY `kategori_id` (`kategori_id`);

--
-- Indexes for table `supplier`
--
ALTER TABLE `supplier`
  ADD PRIMARY KEY (`supplier_id`);

--
-- Indexes for table `transaksi_penjualan`
--
ALTER TABLE `transaksi_penjualan`
  ADD PRIMARY KEY (`transaksi_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `detail_transaksi_penjualan`
--
ALTER TABLE `detail_transaksi_penjualan`
  MODIFY `detail_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `kategori_obat`
--
ALTER TABLE `kategori_obat`
  MODIFY `kategori_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=304;

--
-- AUTO_INCREMENT for table `obat`
--
ALTER TABLE `obat`
  MODIFY `obat_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3003;

--
-- AUTO_INCREMENT for table `supplier`
--
ALTER TABLE `supplier`
  MODIFY `supplier_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=204;

--
-- AUTO_INCREMENT for table `transaksi_penjualan`
--
ALTER TABLE `transaksi_penjualan`
  MODIFY `transaksi_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `detail_transaksi_penjualan`
--
ALTER TABLE `detail_transaksi_penjualan`
  ADD CONSTRAINT `detail_transaksi_penjualan_ibfk_1` FOREIGN KEY (`transaksi_id`) REFERENCES `transaksi_penjualan` (`transaksi_id`),
  ADD CONSTRAINT `detail_transaksi_penjualan_ibfk_2` FOREIGN KEY (`obat_id`) REFERENCES `obat` (`obat_id`);

--
-- Constraints for table `obat`
--
ALTER TABLE `obat`
  ADD CONSTRAINT `obat_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`),
  ADD CONSTRAINT `obat_ibfk_2` FOREIGN KEY (`kategori_id`) REFERENCES `kategori_obat` (`kategori_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

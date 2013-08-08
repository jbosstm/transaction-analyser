INSERT INTO `Transaction` (`id`, `status`, `transactionId`) VALUES
(1, 'IN_FLIGHT', '0:ffff05974e31:-4551a676:519cc32a:58f'),
(2, 'COMMIT', '0:ffff05974e31:-4551a676:519cc32a:590'),
(3, 'ROLLBACK_CLIENT', '0:ffff05974e31:-4551a676:519cc32a:592'),
(4, 'ROLLBACK_RESOURCE', '0:ffff05974e31:-4551a676:519cc32a:593');

INSERT INTO `Event` (`id`, `eventType`, `timestamp`, `transaction_id`) VALUES
(1, 'BEGIN', '2013-05-28 10:45:33', 1),
(2, 'BEGIN', '2013-05-28 10:45:36', 2),
(3, 'BEGIN', '2013-05-28 10:45:37', 3),
(4, 'BEGIN', '2013-05-28 10:47:33', 4),
(5, 'COMMIT', '2013-05-28 10:45:45', 2),
(6, 'ABORT', '2013-05-28 10:45:50', 3),
(7, 'ABORT', '2013-05-28 10:45:52', 4);



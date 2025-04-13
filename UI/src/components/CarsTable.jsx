import {
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Paper, CircularProgress, IconButton, Tooltip, Box, Pagination,
    useMediaQuery, Typography, Select, MenuItem, Chip
} from '@mui/material';
import { Delete, Edit } from '@mui/icons-material';
import { useState, useEffect } from 'react';
import axios from 'axios';
import ConfirmationDialog from './ConfirmationDialog';
import CarDialog from './CarDialog';

export default function CarsTable({ onError, onSuccess, loadRelations, relations  }) {
    const [cars, setCars] = useState([]);
    const [loading, setLoading] = useState(true);
    const [editCar, setEditCar] = useState(null);
    const [deleteTarget, setDeleteTarget] = useState(null);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);
    const [pageSize, setPageSize] = useState(5);
    const isMobile = useMediaQuery('(max-width:600px)');

    const fetchCars = async (pageNumber = page, size = pageSize) => {
        try {
            setLoading(true);
            const { data } = await axios.get(`/api/cars?page=${pageNumber - 1}&size=${size}`);
            setCars(data.content);
            setTotalPages(data.page.totalPages);
        } catch (error) {
            onError('Ошибка загрузки автомобилей');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadRelations();
        fetchCars();
    }, []);

    const handleDelete = async () => {
        try {
            await axios.delete(`/api/cars/${deleteTarget}`);
            onSuccess('Автомобиль успешно удален');
            await fetchCars();
        } catch (error) {
            onError('Ошибка удаления автомобиля');
        }
        setDeleteTarget(null);
    };

    const handlePageChange = (event, value) => {
        setPage(value);
        fetchCars(value);
    };
    if (loading) return <CircularProgress sx={{ mt: 3 }} />;

    return (
        <Box sx={{
            overflowX: 'auto',
            maxHeight: '70vh',
            overflowY: 'auto',
            position: 'relative',
            '& .MuiTableContainer-root': {
                backgroundColor: 'transparent !important',
                boxShadow: 'none !important'
            } }}>
            <TableContainer component={Paper} sx={{
                background: 'rgba(255, 255, 255, 0.7)', // Полупрозрачный белый
                backdropFilter: 'blur(8px)', // Эффект размытия
                boxShadow: '0 4px 20px rgba(0,0,0,0.08)', // Статичная тень
                '&:hover': { boxShadow: '0 4px 20px rgba(0,0,0,0.08)' } // Убираем изменение при наведении
            }}>
                <Table
                    size={isMobile ? 'small' : 'medium'}
                    sx={{
                        '& .MuiTableRow-root': {
                            backgroundColor: 'rgba(255, 255, 255, 0.4)', // Единый полупрозрачный фон
                            transition: 'background-color 0.3s'
                        },
                        '& .MuiTableRow-root:hover': {
                            backgroundColor: 'rgba(245, 245, 245, 0.7)' // Легкое затемнение при наведении
                        }
                    }}
                >
                    <TableHead sx={{
                        background: 'rgba(63,81,181,0.1)', // Полупрозрачный фон
                        backdropFilter: 'blur(4px)', // Размытие
                        borderBottom: '2px solid rgba(63,81,181,0.2)'
                    }}>
                        <TableRow>
                            <TableCell sx={{ color: 'black' }}>Автомобиль</TableCell>
                            {!isMobile && <TableCell sx={{ color: 'black' }}>Владелец</TableCell>}
                            <TableCell sx={{ color: 'black' }}>Действия</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {cars.map(car => (
                            <TableRow key={car.id}>
                                <TableCell>
                                    <Box>
                                        <Typography variant="body1">
                                            {car.brand} {car.model}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            Мощность: {car.power} л.с.
                                        </Typography>
                                        {isMobile && car.owner && (
                                            <Chip
                                                label={car.owner.name}
                                                size="small"
                                                sx={{
                                                    background: 'rgba(63,81,181,0.1)',
                                                    backdropFilter: 'blur(4px)',
                                                    border: '1px solid rgba(63,81,181,0.3)',
                                                    color: 'black',
                                                    mt: 0.5
                                                }}
                                            />
                                        )}
                                    </Box>
                                </TableCell>

                                {!isMobile && (
                                    <TableCell>
                                        {car.owner ? (
                                            <Chip
                                                label={car.owner.name}
                                                size="small"
                                                sx={{
                                                    background: 'rgba(63,81,181,0.1)',
                                                    backdropFilter: 'blur(4px)',
                                                    border: '1px solid rgba(63,81,181,0.3)',
                                                    color: 'black'
                                                }}
                                            />
                                        ) : (
                                            <Typography variant="body2" color="text.disabled">
                                                Нет владельца
                                            </Typography>
                                        )}
                                    </TableCell>
                                )}

                                <TableCell>
                                    <Box sx={{ display: 'flex', gap: 1 }}>
                                        <Tooltip title="Редактировать">
                                            <IconButton
                                                onClick={async () => {
                                                    await loadRelations();
                                                    setEditCar(car);
                                                }}
                                                size={isMobile ? 'small' : 'medium'}
                                                sx={{
                                                    '&:hover svg': {
                                                        transform: 'rotate(15deg)',
                                                        transition: 'transform 0.3s'
                                                    }
                                                }}
                                            >
                                                <Edit fontSize={isMobile ? 'small' : 'medium'} />
                                            </IconButton>
                                        </Tooltip>
                                        <Tooltip title="Удалить">
                                            <IconButton
                                                onClick={() => setDeleteTarget(car.id)}
                                                color="error"
                                                size={isMobile ? 'small' : 'medium'}
                                                sx={{
                                                    '&:hover svg': {
                                                        transform: 'scale(1.2)',
                                                        transition: 'transform 0.3s'
                                                    }
                                                }}
                                            >
                                                <Delete fontSize={isMobile ? 'small' : 'medium'} />
                                            </IconButton>
                                        </Tooltip>
                                    </Box>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            <Box sx={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                mt: 2,
                p: 1,
                background: 'rgba(255,255,255,0.9)',
                borderRadius: 2,
                boxShadow: 1
            }}>
                <Pagination
                    count={totalPages}
                    page={page}
                    onChange={handlePageChange}
                    sx={{ flexGrow: 1 }}
                    size={isMobile ? 'small' : 'medium'}
                    showFirstButton
                    showLastButton
                />

                <Select
                    value={pageSize}
                    onChange={(e) => {
                        const newSize = Number(e.target.value);
                        setPageSize(newSize);
                        setPage(1);
                        fetchCars(1, newSize);
                    }}
                    size={isMobile ? 'small' : 'medium'}
                    sx={{ ml: 2, minWidth: 120 }}
                >
                    <MenuItem value={5}>5</MenuItem>
                    <MenuItem value={10}>10</MenuItem>
                    <MenuItem value={20}>20</MenuItem>
                </Select>
            </Box>

            <CarDialog
                open={!!editCar}
                onClose={() => setEditCar(null)}
                carToEdit={editCar}
                onError={onError}
                onSuccess={(msg) => {
                    onSuccess(msg);
                    fetchCars();
                }}
                relations={relations}
            />

            <ConfirmationDialog
                open={!!deleteTarget}
                onClose={() => setDeleteTarget(null)}
                onConfirm={handleDelete}
                title="Удаление автомобиля"
                content="Вы уверены, что хотите удалить этот автомобиль?"
            />
        </Box>
    );
}
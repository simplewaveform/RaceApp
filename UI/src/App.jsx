import { Container, CssBaseline, Fab, Tabs, Tab, Box, Snackbar, Alert } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import { useState, useEffect } from 'react';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import PilotsTable from './components/PilotsTable';
import CarsTable from './components/CarsTable';
import RacesTable from './components/RacesTable';
import PilotDialog from './components/PilotDialog';
import CarDialog from './components/CarDialog';
import RaceDialog from './components/RaceDialog';
import axios from 'axios';

const theme = createTheme({
    palette: {
        mode: 'dark', // Включаем тёмный режим
        primary: {
            main: '#90caf9', // Голубой акцентный цвет
        },
        secondary: {
            main: '#f48fb1', // Розовый акцентный цвет
        },
        background: {
            default: '#121212', // Основной тёмный фон
            paper: '#1e1e1e', // Цвет для компонентов Paper
        },
        text: {
            primary: '#ffffff', // Основной белый текст
            secondary: 'rgba(255, 255, 255, 0.7)', // Вторичный текст
        },
    },
    breakpoints: {
        values: {
            xs: 0,
            sm: 600,
            md: 900,
            lg: 1200,
            xl: 1536,
        },
    },
    components: {
        MuiMenuItem: {
            styleOverrides: {
                root: {
                    color: '#fff !important',
                    backgroundColor: '#1e1e1e !important',
                    '&:hover': {
                        backgroundColor: 'rgba(63, 81, 181, 0.5) !important'
                    },
                    '&.Mui-selected': {
                        backgroundColor: '#3f51b5 !important'
                    }
                }
            }
        },
        MuiTableCell: {
            styleOverrides: {
                root: {
                    color: '#ffffff !important',
                    borderBottom: '1px solid rgba(255, 255, 255, 0.12)'
                }
            }
        },
        MuiButton: {
            styleOverrides: {
                root: {
                    transition: 'all 0.3s ease',
                    '&:hover': {
                        transform: 'translateY(-2px)',
                        boxShadow: 3
                    },
                    '@media (max-width:600px)': {
                        minWidth: '32px',
                        padding: '4px'
                    }
                }
            }
        },
        MuiPagination: {
            styleOverrides: {
                root: {
                    color: '#fff',
                    '& .MuiButtonBase-root': {
                        color: '#fff',
                        border: '1px solid rgba(255, 255, 255, 0.23)',
                        '&:hover': {
                            backgroundColor: 'rgba(255, 255, 255, 0.08)'
                        }
                    },
                    '& .Mui-selected': {
                        backgroundColor: '#3f51b5 !important',
                        color: '#fff !important',
                        borderColor: 'transparent'
                    }
                }
            }
        },
        MuiPaper: {
            styleOverrides: {
                root: {
                    backgroundColor: 'rgba(0, 0, 0, 0.1)', // Тёмный полупрозрачный фон
                    backdropFilter: 'blur(12px)',
                    border: '1px solid rgba(255, 255, 255, 0.12)' // Светлая граница
                }
            }
        },
        MuiSelect: {
            styleOverrides: {
                select: {
                    color: '#fff',
                    backgroundColor: 'rgba(255, 255, 255, 0.1)',
                    '&:focus': {
                        backgroundColor: 'rgba(255, 255, 255, 0.1)'
                    }
                },
                icon: {
                    color: '#fff'
                }
            }
        },
        MuiInputBase: {
            styleOverrides: {
                root: {
                    color: '#fff',
                    '&::before': {
                        borderBottom: '1px solid rgba(255, 255, 255, 0.7)'
                    }
                }
            }
        }
    }
});

const tabBackgrounds = [
    '/images/pilots-bg.jpg',
    '/images/cars-bg.jpg',
    '/images/races-bg.jpg'
];

export default function App() {
    const [currentTab, setCurrentTab] = useState(0);
    const [dialogOpen, setDialogOpen] = useState(null);
    const [snackbar, setSnackbar] = useState({
        open: false,
        message: '',
        severity: 'success'
    });
    const [relations, setRelations] = useState({
        pilots: [],
        cars: []
    });

    const tabs = ['pilots', 'cars', 'races'];

    const loadRelations = async () => {
        try {
            const [pilotsRes, carsRes] = await Promise.all([
                axios.get('/api/pilots?size=1000'),
                axios.get('/api/cars?size=1000')
            ]);

            setRelations({
                pilots: pilotsRes.data.content || [],
                cars: carsRes.data.content || []
            });
        } catch (error) {
            showError('Ошибка загрузки данных');
        }
    };

    const showError = (message) => {
        setSnackbar({ open: true, message, severity: 'error' });
    };

    const showSuccess = (message) => {
        setSnackbar({ open: true, message, severity: 'success' });
    };

    useEffect(() => {
        loadRelations();
    }, []);

    useEffect(() => {
        loadRelations();
    }, [currentTab]);

    return (
        <ThemeProvider theme={theme}>
            <Container component="main" maxWidth="xl" sx={{
                py: 2,
                position: 'relative',
                minHeight: '100vh'
            }}>
                <CssBaseline />

                <Box sx={{
                    position: 'fixed',
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0,
                    backgroundImage: `url(${tabBackgrounds[currentTab]})`,
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                    backgroundAttachment: 'fixed',
                    opacity: 0.5,
                    zIndex: 0
                }} />

                <Tabs
                    value={currentTab}
                    onChange={(e, v) => setCurrentTab(v)}
                    sx={{
                        mb: 2,
                        position: 'relative',
                        zIndex: 2,
                        '& .MuiTabs-indicator': {
                            background: 'linear-gradient(45deg, #2f51b5 30%, #2196f3 90%)'
                        }
                    }}
                    variant="scrollable"
                    scrollButtons="auto"
                >
                    <Tab label="Пилоты" sx={{
                        textTransform: 'none',
                        fontSize: 16,
                        color: theme.palette.getContrastText(theme.palette.background.default)
                    }} />
                    <Tab label="Автомобили" sx={{
                        textTransform: 'none',
                        fontSize: 16,
                        color: theme.palette.getContrastText(theme.palette.background.default)
                    }} />
                    <Tab label="Гонки" sx={{
                        textTransform: 'none',
                        fontSize: 16,
                        color: theme.palette.getContrastText(theme.palette.background.default)
                    }} />
                </Tabs>

                <Box sx={{
                    pt: 2,
                    position: 'relative',
                    zIndex: 1,
                    maxHeight: 'calc(100vh - 180px)',
                    overflowY: 'auto'
                }}>
                    {currentTab === 0 && (
                        <PilotsTable
                            onError={showError}
                            onSuccess={showSuccess}
                            sx={{ backdropFilter: 'blur(8px)' }}
                        />
                    )}

                    {currentTab === 1 && (
                        <CarsTable
                            onError={showError}
                            onSuccess={showSuccess}
                            relations={relations}
                            loadRelations={loadRelations}
                        />
                    )}

                    {currentTab === 2 && (
                        <RacesTable
                            onError={showError}
                            onSuccess={showSuccess}
                            relations={relations}
                            loadRelations={loadRelations}
                        />
                    )}
                </Box>

                <Fab
                    sx={{
                        position: 'fixed',
                        top: 16,
                        right: 16,
                        background: 'linear-gradient(45deg, #3f51b5 30%, #2196f3 90%)',
                        transition: 'transform 0.3s',
                        zIndex: 3,
                        '&:hover': {
                            transform: 'scale(1.2)'
                        }
                    }}
                    onClick={async () => {
                        await loadRelations();
                        setDialogOpen(tabs[currentTab]);
                    }}
                >
                    <AddIcon sx={{ color: 'white' }} />
                </Fab>

                {currentTab === 0 && (
                    <PilotDialog
                        open={dialogOpen === 'pilots'}
                        onClose={() => setDialogOpen(null)}
                        onSuccess={(msg) => {
                            showSuccess(msg);
                            setDialogOpen(null);
                            loadRelations();
                        }}
                        onError={showError}
                    />
                )}

                {currentTab === 1 && (
                    <CarDialog
                        open={dialogOpen === 'cars'}
                        onClose={() => setDialogOpen(null)}
                        onSuccess={(msg) => {
                            showSuccess(msg);
                            setDialogOpen(null);
                            loadRelations();
                        }}
                        relations={relations}
                        onError={showError}
                    />
                )}

                <RaceDialog
                    open={dialogOpen === 'races'}
                    onClose={() => setDialogOpen(null)}
                    relations={relations}
                    onError={showError}
                    onSuccess={(msg) => {
                        showSuccess(msg);
                        setDialogOpen(null);
                        loadRelations();
                    }}
                />

                <Snackbar
                    open={snackbar.open}
                    autoHideDuration={6000}
                    onClose={() => setSnackbar(s => ({ ...s, open: false }))}
                    anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                    sx={{ zIndex: 4 }}
                >
                    <Alert
                        severity={snackbar.severity}
                        sx={{
                            animation: 'slideIn 0.3s ease-out',
                            borderRadius: 2,
                            boxShadow: 3,
                            '@keyframes slideIn': {
                                from: { transform: 'translateX(100%)' },
                                to: { transform: 'translateX(0)' }
                            }
                        }}
                    >
                        {snackbar.message}
                    </Alert>
                </Snackbar>
            </Container>
        </ThemeProvider>
    );
}